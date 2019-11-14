package com.goldenstraw.restaurant.goodsmanager.ui.verify

import android.app.PendingIntent
import android.content.Intent
import android.telephony.SmsManager
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
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
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
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
        getAllOrderOfDate(TimeConverter.getCurrentDateString(), 0)

        fab_send_to_supplier.hide()

        initEvent()

        initSwipeMenu()


    }

    private fun initEvent() {
        radio_district.setOnCheckedChangeListener { group, checkedId ->
            showList.clear()
            when (checkedId) {
                R.id.rb_xishinan_district -> {
                    showList.addAll(ordersOfXingShiNan)
                }
                R.id.rb_xishan_district -> {
                    showList.addAll(ordersOfXiShan)
                }
            }
            if (showList.isNotEmpty()) {
                state.set(MultiStateView.VIEW_STATE_CONTENT)
                fab_send_to_supplier.show()
            } else {
                state.set(MultiStateView.VIEW_STATE_EMPTY)
                fab_send_to_supplier.hide()
            }
            adapter!!.forceUpdate()
        }

    }
    /**
     * 初始化Item侧滑菜单,只有修改
     */
    private fun initSwipeMenu() {
        /*
        1、生成子菜单，这里将子菜单设置在右侧
         */
        val mSwipeMenuCreator = SwipeMenuCreator { leftMenu, rightMenu, position ->

            val updateItem = SwipeMenuItem(this)
                .setBackground(R.color.secondaryColor)
                .setText("修改")
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setWidth(200)
            rightMenu.addMenuItem(updateItem)
        }
        /*
         2、关联RecyclerView，设置侧滑菜单
         */
        rlw_orders_of_district.setSwipeMenuCreator(mSwipeMenuCreator)
        /*
        3、定义子菜单点击事件
         */
        val mItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (menuBridge.position) {
                0 -> {
                    if (showList[adapterPosition].state == 0)
                        updateDialog(showList[adapterPosition])
                    else
                        toast { "该订单已生成不能修改！" }
                }
            }
        }
        /*
        4、给RecyclerView添加监听器
         */
        rlw_orders_of_district.setOnItemMenuClickListener(mItemMenuClickListener)
    }

    private fun updateDialog(order: OrderItem) {
        val view = layoutInflater.inflate(R.layout.only_input_number_dialog_view, null)
        val quantity = view.findViewById<EditText>(R.id.number_edit)
        quantity.setText(order.quantity.toString())
        val dialog = android.app.AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_update_name)
            .setTitle("修改购买数量")
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                var newQuantity = quantity.text.toString()
                if (newQuantity.isNullOrEmpty()) {
                    newQuantity = "0.0f"
                }
                order.quantity = newQuantity.toFloat()
                viewModel!!.updateOrderItemQuantity(order)
                adapter!!.forceUpdate()
                dialog.dismiss()

            }.create()
        dialog.show()
    }


    /**
     * 获取订单信息
     * 将来可以按类别进行分组
     */
    private fun getAllOrderOfDate(date: String, stauts: Int) {
        val condition =
            "{\"\$and\":[{\"orderDate\":\"$date\"},{\"state\":$stauts}]}"
        viewModel!!.getAllOrderOfDate(condition)
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
        var supplier = ""
        //1、创建一个已选择的列表
        val selectedList = mutableListOf<OrderItem>()
        showList.forEach {
            if (it.isSelected) {
                it.supplier = supplier
                selectedList.add(it)
            }
        }
        if (selectedList.isEmpty()) {
            toast { "请选择选择商品！！" }
            return
        }
        showDialog(supplier, selectedList)

    }

    private fun showDialog(supplier: String, selectedList: MutableList<OrderItem>) {
        var supplier1 = supplier
        val view = layoutInflater.inflate(R.layout.select_supplier_view, null)
        val spinner = view.findViewById<AppCompatSpinner>(R.id.spinner_select_supplier)
        spinner.adapter = SupplierSpinnerAdapter(this, viewModel!!.suppliers)
        spinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                supplier1 = viewModel!!.suppliers[position].username.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })
        val dialog = AlertDialog.Builder(this)
            .setIcon(R.mipmap.add_icon)
            .setTitle("请选择供应商")
            .setView(view)
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                sendOrderToSupplier(supplier1, selectedList)
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    /**
     * 将订单发送给对应的供应商，刷新列表
     */

    private fun sendOrderToSupplier(supplier: String, selectedList: MutableList<OrderItem>) {

        viewModel!!.transOrdersToBatchRequestObject(selectedList, supplier)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({ it ->
                viewModel!!.sendToOrderToSupplier(it)
                    .subscribeOn(Schedulers.io())
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