package com.goldenstraw.restaurant.goodsmanager.ui.verify

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.owner.basemodule.room.entities.User
import com.owner.basemodule.util.TimeConverter
import com.owner.basemodule.util.toast
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_verify_place_orders.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 审核订单，并发送订单
 */

class VerifyAndSendOrderActivity : BaseActivity<ActivityVerifyPlaceOrdersBinding>() {

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
        /**
         * 获取发送短信的权限
         */
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 1)
        }
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
                binding.longClick = object : Consumer<OrderItem> {
                    override fun accept(t: OrderItem) {
                        managerDialog(t)
                    }
                }
            }
        )
        getAllOrderOfDate(TimeConverter.getCurrentDateString(), 0)

        fab_send_to_supplier.hide()

        initEvent()

    }

    private fun initEvent() {
        radio_district.setOnCheckedChangeListener { group, checkedId ->
            showList.clear()
            when (checkedId) {
                R.id.rb_xishinan_district -> {
                    showList.addAll(viewModel!!.ordersList.filter {
                        it.district == 0
                    })

                }
                R.id.rb_xishan_district -> {
                    showList.addAll(viewModel!!.ordersList.filter {
                        it.district == 1
                    })
                }
            }
            if (showList.isNotEmpty()) {
                fab_send_to_supplier.show()
            } else {
                viewModel!!.viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                fab_send_to_supplier.hide()
            }
            adapter!!.forceUpdate()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    toast { "您没有发送短信和权限！" }
                }
            }
        }
    }

    fun sendOrderItemSMS(address: String, content: String) {
        val manager = SmsManager.getDefault()
        val intent = Intent("com.android.TinySMS.RESULT")
        val sentIntent = PendingIntent.getBroadcast(
            this, 0,
            intent, PendingIntent.FLAG_ONE_SHOT
        )
        manager.sendTextMessage(address, null, content, sentIntent, null)
    }

    /**
    管理数据
     */
    private fun managerDialog(orders: OrderItem) {
        val view = layoutInflater.inflate(R.layout.delete_or_update_dialog_view, null)
        val delete = view.findViewById<Button>(R.id.delete_action)
        delete.visibility = View.GONE
        val update = view.findViewById<Button>(R.id.update_action)

        val managerDialog = android.app.AlertDialog.Builder(this)
            .setView(view)
            .create()
        managerDialog.show()
        update.setOnClickListener {
            if (orders.state == 0)
                updateDialog(orders)
            else
                toast { "该订单已生成不能修改！" }
            managerDialog.dismiss()
        }
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
                if (newQuantity.isEmpty()) {
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
        viewModel!!.getOrdersOfDate(condition)

    }


    /**
     * 创建供应商单选对话框
     */
    fun popUpSelectSupplierDialog() {
        val supplier = ""
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

        var supplier1: User = viewModel!!.suppliers[0]
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
                supplier1 = viewModel!!.suppliers[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })
        val dialog = AlertDialog.Builder(this)
            .setIcon(R.mipmap.add_icon)
            .setTitle("请选择供应商")
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                sendOrderToSupplier(supplier1, selectedList)
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    /**
     * 将订单发送给对应的供应商，刷新列表
     */

    private fun sendOrderToSupplier(supplier: User, selectedList: MutableList<OrderItem>) {

        viewModel!!.transOrdersToBatchRequestObject(selectedList, supplier.username!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({ it ->
                viewModel!!.sendToOrderToSupplier(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .autoDisposable(scopeProvider)
                    .subscribe({
                        sendOrderItemSMS(
                            "${supplier.mobilePhoneNumber}",
                            "税务学校采购订单，共有${selectedList.size}项内容，注意查看！！"
                        )
                    }, { error ->
                        toast { "批量修改" + error.message }
                    })
            }, { mess ->
                toast { mess.message.toString() }
            }, {
                viewModel!!.ordersList.removeAll(selectedList)
                showList.removeAll(selectedList)
                adapter!!.forceUpdate()
            })

    }

}