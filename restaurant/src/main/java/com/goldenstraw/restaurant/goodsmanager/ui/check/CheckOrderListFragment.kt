package com.goldenstraw.restaurant.goodsmanager.ui.check

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCheckOrderListBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectCheckGoods
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import kotlinx.android.synthetic.main.fragment_check_order_list.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.text.DecimalFormat

class CheckOrderListFragment : BaseFragment<FragmentCheckOrderListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_check_order_list

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    private val repository by instance<VerifyAndPlaceOrderRepository>()

    lateinit var viewModel: VerifyAndPlaceOrderViewModel


    var adapter = BaseDataBindingAdapter(

        layoutId = R.layout.layout_order_item,
        dataSource = { showList },
        dataBinding = { LayoutOrderItemBinding.bind(it) },
        callback = { order, binding, _ ->

            binding.orderitem = order

            binding.clickEvent = object : Consumer<OrderItem> {
                override fun accept(t: OrderItem) {
                    //弹出修改数量的窗口
                    if (t.state == 1)
                        popUpCheckQuantityDialog(t)
                }
            }

            binding.longClick = object : Consumer<OrderItem> {
                override fun accept(t: OrderItem) {
                    managerDialog(t)
                }
            }
        }

    )

    var supplier = ""
    var orderDate = ""
    var orderState = 0
    var district = 0

    //需要显示的数据列表
    var showList = mutableListOf<OrderItem>()

    override fun initView() {
        super.initView()
        supplier = arguments!!.getString("supplier")!!
        orderDate = arguments!!.getString("orderDate")!!
        orderState = arguments!!.getInt("orderState")
        district = arguments!!.getInt("district")

        check_order_toolbar.title = supplier
        check_order_toolbar.subtitle = orderDate
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
        viewModel.defUI.refreshEvent.observe(viewLifecycleOwner) {
            getOrderItemList()
            adapter.forceUpdate()
        }
        getOrderItemList()
    }

    /**
     * 弹出确认实际数量的窗口
     */
    fun popUpCheckQuantityDialog(orderItem: OrderItem) {
        val view =
            LayoutInflater.from(context).inflate(R.layout.only_input_number_dialog_view, null)
        val edit = view.findViewById<EditText>(R.id.number_edit)
        val dialog = AlertDialog.Builder(context!!)
            .setTitle("确定\"${orderItem.goodsName}\"的数量")
            .setIcon(R.mipmap.add_icon)
            .setView(view)
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                if (edit.text.isNullOrEmpty()) {
                    return@setPositiveButton
                }
                val check = edit.text.toString().trim().toFloat()
                saveCheckResult(check, orderItem)
                dialog.dismiss()
            }.create()
        dialog.show()

    }


    /**
     * 查看状态由菜单控制，可以是1送货状态，也可以2已验状态。
     */
    private fun getOrderItemList() {

        showList = when (orderState) {

            1 -> viewModel.ordersList.filter {
                it.supplier == supplier && it.state == orderState
            } as MutableList

            2 -> viewModel.ordersList.filter {
                it.supplier == supplier && it.state != 1
            } as MutableList
            else -> mutableListOf()
        }

        adapter.forceUpdate()
    }

    /****************************************************
     *长按事件；管理数据。修改和删除功能
     *****************************************************/
    private fun managerDialog(orders: OrderItem) {
        val view = layoutInflater.inflate(R.layout.delete_or_update_dialog_view, null)
        val delete = view.findViewById<Button>(R.id.delete_action)
        delete.text = "退货"
        val update = view.findViewById<Button>(R.id.update_action)
        update.text = "重验"
        val managerDialog = android.app.AlertDialog.Builder(context)
            .setView(view)
            .create()
        managerDialog.show()
        delete.setOnClickListener {
            deleteDialog(orders)
            managerDialog.dismiss()
        }
        update.setOnClickListener {
            updateDialog(orders)
            managerDialog.dismiss()
        }
    }

    private fun deleteDialog(orders: OrderItem) {
        val dialog = AlertDialog.Builder(context!!)
            .setTitle("确定\"${orders.goodsName}\"退货吗？")
            .setIcon(R.mipmap.add_icon)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                returnedGoods(orders)
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    private fun updateDialog(orders: OrderItem) {
        if (orders.state == 3) {
            Toast.makeText(context, "已经确认不能重新验收！！", Toast.LENGTH_SHORT).show()

        } else {
            val dialog = AlertDialog.Builder(context!!)
                .setTitle("确定对\"${orders.goodsName}\"重新验收吗？")
                .setIcon(R.mipmap.add_icon)
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("确定") { dialog, _ ->
                    cancelChecked(orders)
                    dialog.dismiss()
                }.create()
            dialog.show()
        }
    }


    /*
     * 保存验货结果
     */
    private fun saveCheckResult(
        check: Float,
        orderItem: OrderItem
    ) {
        val format = DecimalFormat(".00")
        val total = format.format(check * orderItem.unitPrice).toFloat()
        val newQuantity = ObjectCheckGoods(
            quantity = orderItem.quantity,
            checkQuantity = check,
            againCheckQuantity = check,
            total = total,
            state = 2
        )
        viewModel.setCheckQuantity(newQuantity, orderItem)

    }

    /*
     * 重验
     */
    private fun cancelChecked(orderItem: OrderItem) {
        val again = ObjectCheckGoods(orderItem.quantity, 0.0f, 0.0f, 0.0f, 1)
        viewModel!!.setCheckQuantity(again, orderItem)

    }

    /*
     * 退货,状态改为-1。
     */
    private fun returnedGoods(orderItem: OrderItem) {
        val returned = ObjectCheckGoods(orderItem.quantity, 0.0f, 0.0f, 0.0f, -1)
        viewModel!!.setCheckQuantity(returned, orderItem)

    }
}