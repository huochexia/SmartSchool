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
 * 审核报货单，并发送订单给供货商
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

    private var showList = mutableListOf<OrderItem>() //用于显示的列表，两个校区分别通过这个变量显示

    val adapter = BaseDataBindingAdapter(
        layoutId = R.layout.layout_order_item,
        dataSource = { showList },
        dataBinding = { LayoutOrderItemBinding.bind(it) },
        callback = { order, binding, _ ->
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
        viewModel!!.defUI.refreshEvent.observe(this) {
            //数据加载完成，默认选择
            rb_xishinan_district.isChecked = true
            //数据加载成功了，获取供应商信息
            viewModel!!.getAllSupplier()
        }
        viewModel!!.defUI.showDialog.observe(this) {
            AlertDialog.Builder(this)
                .setMessage(it)
                .create()
                .show()
        }
        getAllOrderOfDate(TimeConverter.getCurrentDateString())

        initEvent()

    }

    private fun initEvent() {
        radio_district.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_xishinan_district -> {
                    showList = (viewModel!!.ordersList.filter {
                        it.district == 0
                    }) as MutableList<OrderItem>
                }
                R.id.rb_xishan_district -> {
                    showList = (viewModel!!.ordersList.filter {
                        it.district == 1
                    }) as MutableList<OrderItem>
                }
            }
            if (showList.isNotEmpty()) {
                viewModel!!.viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                fab_send_to_supplier.show()
            } else {
                viewModel!!.viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                fab_send_to_supplier.hide()
            }
            adapter.forceUpdate()
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

    /************************************************************
     * 获取订单信息，此时订单状态为0。
     *
     ***********************************************************/
    private fun getAllOrderOfDate(date: String) {
        val condition =
            "{\"\$and\":[{\"orderDate\":\"$date\"},{\"state\":0}]}"
        viewModel!!.getOrdersOfCondition(condition)

    }

    /***************************************************
     * 长按事件：管理数据，对订单进行修改
     ***************************************************/
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
        val view = layoutInflater.inflate(R.layout.input_number_and_note_dialog_view, null)
        val quantity = view.findViewById<EditText>(R.id.ed_number)
        val note = view.findViewById<EditText>(R.id.ed_note)
        quantity.setText(order.quantity.toString())
        note.setText(order.note)
        val dialog = android.app.AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_update_name)
            .setTitle("修改订单")
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
                order.note = note.text.toString()
                viewModel!!.updateOrderItem(order)
                adapter.forceUpdate()
                dialog.dismiss()

            }.create()
        dialog.show()
    }


    /*********************************************************
     * 发送订单，也就是给订单添加供应商，状态设置为1的过程
     *********************************************************/
    /*
    获取供应商
     */
    fun popUpSelectSupplierDialog() {

        val selectedList =
            showList.filter {
                it.isSelected
            } as MutableList<OrderItem>
        if (selectedList.isEmpty()) {
            toast { "请选择选择商品！！" }
            return
        }
        showDialog(selectedList)

    }

    private fun showDialog(selectedList: MutableList<OrderItem>) {

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

    /*
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
                adapter.forceUpdate()
            })

    }

    /*
    发送短信
     */
    private fun sendOrderItemSMS(address: String, content: String) {
        val manager = SmsManager.getDefault()
        val intent = Intent("com.android.TinySMS.RESULT")
        val sentIntent = PendingIntent.getBroadcast(
            this, 0,
            intent, PendingIntent.FLAG_ONE_SHOT
        )
        manager.sendTextMessage(address, null, content, sentIntent, null)
    }
}