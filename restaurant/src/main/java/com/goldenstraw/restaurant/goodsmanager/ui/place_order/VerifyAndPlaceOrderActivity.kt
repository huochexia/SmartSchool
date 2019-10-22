package com.goldenstraw.restaurant.goodsmanager.ui.place_order

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityVerifyPlaceOrdersBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.goodsmanager.adapter.SupplierSpinnerAdapter
import com.goldenstraw.restaurant.goodsmanager.di.verifyandplaceorderdatasource
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.util.TimeConverter
import com.owner.basemodule.util.toast
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_verify_place_orders.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance


class VerifyAndPlaceOrderActivity : BaseActivity<ActivityVerifyPlaceOrdersBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_verify_place_orders
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(verifyandplaceorderdatasource)
    }

    val repository: VerifyAndPlaceOrderRepository by instance()
    var viewModel: VerifyAndPlaceOrderViewModel? = null

    var showList = mutableListOf<OrderItem>() //用于显示的列表
    private val ordersOfXingShiNan = mutableListOf<OrderItem>() //新石南路的订单
    private val ordersOfXiShan = mutableListOf<OrderItem>()  //西山校区
    var adapter: BaseDataBindingAdapter<OrderItem, LayoutOrderItemBinding>? = null

    val state = ObservableField<Int>()

    override fun initView() {
        super.initView()
        setSupportActionBar(toolbar)//没有这个显示不了菜单
        viewModel = getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_order_item,
            dataSource = { showList },
            dataBinding = { LayoutOrderItemBinding.bind(it) },
            callback = { order, binding, position ->
                binding.orderitem = order
                binding.checkEvent = object : Consumer<OrderItem> {
                    override fun accept(t: OrderItem) {
                        t.isSelected = !t.isSelected
                        order.isSelected = t.isSelected
                    }
                }
                binding.cbGoods.isChecked = order.isSelected

            }
        )
        getAllOrderOfDate(TimeConverter.getCurrentDateString())
        tv_show_district.text = "选择校区"
        fab_send_to_supplier.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_select_district, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.select_xinshinan_district -> {
                tv_show_district.text = "新石南路校区"
                showList.clear()
                showList.addAll(ordersOfXingShiNan)
                if (showList.isNotEmpty())
                    fab_send_to_supplier.show()
                adapter!!.forceUpdate()
            }
            R.id.select_xishan_district -> {

                tv_show_district.text = "西山校区"
                showList.clear()
                showList.addAll(ordersOfXiShan)
                if (showList.isNotEmpty())
                    fab_send_to_supplier.show()
                adapter!!.forceUpdate()
            }
        }
        return true
    }

    /**
     * 获取订单信息
     * 将来可以按类别进行分组
     */
    private fun getAllOrderOfDate(date: String) {
        viewModel!!.getAllOrderOfDate(date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                groupByDistrictOrders(it)
                state.set(MultiStateView.VIEW_STATE_CONTENT)
            }, {
                toast { it.message.toString() }
                state.set(MultiStateView.VIEW_STATE_ERROR)
            }, {

            }, {
                state.set(MultiStateView.VIEW_STATE_LOADING)
            })
    }

    /**
     * 将所有订单进行分组
     */
    private fun groupByDistrictOrders(orderList: MutableList<OrderItem>) {
        ordersOfXiShan.clear()
        ordersOfXingShiNan.clear()
        orderList.forEach {
            when (it.district) {
                0 -> ordersOfXingShiNan.add(it)
                1 -> ordersOfXiShan.add(it)
            }
        }
    }

    /**
     * 创建供应商单选对话框
     */
    fun popUpSelectSupplierDialog() {
        val view = layoutInflater.inflate(R.layout.select_supplier_view, null)
        val supplierList = mutableListOf<String>()
        viewModel!!.suppliers.forEach {
            supplierList.add(it.username.toString())
        }

        var supplier = ""
        val dialog = AlertDialog.Builder(this)
            .setIcon(R.mipmap.add_icon)
            .setTitle("请选择供应商")
            .setView(view)
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                sendOrderToSupplier(supplier)
                if (showList.isEmpty()) {
                    fab_send_to_supplier.hide()
                }
                dialog.dismiss()
            }.create()
        dialog.show()

    }

    /**
     * 将订单发送给对应的供应商，刷新列表
     */

    private fun sendOrderToSupplier(supplier: String) {
        //1、创建一个已选择的列表
        val selectedList = mutableListOf<OrderItem>()
        showList.forEach {
            if (it.isSelected) {
                it.supplier = supplier
                selectedList.add(it)
            }
        }
        viewModel!!.transOrdersToBatchRequestObject(selectedList, supplier)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({ it ->
                viewModel!!.sendToOrderToSupplier(it).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .autoDisposable(scopeProvider)
                    .subscribe({

                    }, { error ->
                        toast { "批量修改" + error.message }
                    })
            }, { mess ->
                toast { mess.message.toString() }
            }, {
                showList.removeAll(selectedList)
                ordersOfXingShiNan.removeAll(selectedList)
                ordersOfXiShan.removeAll(selectedList)
                adapter!!.forceUpdate()
            })

    }
}