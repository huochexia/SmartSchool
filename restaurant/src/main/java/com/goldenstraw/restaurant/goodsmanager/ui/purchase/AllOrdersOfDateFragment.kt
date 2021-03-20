package com.goldenstraw.restaurant.goodsmanager.ui.purchase

import android.app.AlertDialog.Builder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.R.drawable
import com.goldenstraw.restaurant.R.layout
import com.goldenstraw.restaurant.databinding.FragmentAllOrdersOfDateBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.queryordersactivitymodule
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectQuantityAndNote
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.util.toast
import org.kodein.di.Copy.All
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class AllOrdersOfDateFragment : BaseFragment<FragmentAllOrdersOfDateBinding>() {

    override val layoutId: Int
        get() = layout.fragment_all_orders_of_date

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = All)
        import(queryordersactivitymodule)
    }


    private val repository by instance<QueryOrdersRepository>()

    var viewModel: QueryOrdersViewModel? = null


    var adapter = BaseDataBindingAdapter(
        layoutId = layout.layout_order_item,
        dataSource = {
            viewModel!!.ordersList
        },
        dataBinding = { LayoutOrderItemBinding.bind(it) },
        callback = { order, binding, position ->
            binding.orderitem = order

            binding.longClick = object :Consumer<OrderItem>{
                override fun accept(t: OrderItem) {
                    managerDialog(t)
                }
            }
        }

    )


    var orderDate = ""

    override fun initView() {
        super.initView()

        arguments?.let {
            orderDate = it.getString("orderDate")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }


        val where = "{\"orderDate\":\"$orderDate\"}"
        viewModel!!.getAllOfOrders(where)

        viewModel!!.defUI.refreshEvent.observe(viewLifecycleOwner) {
            adapter.forceUpdate()
        }

    }

    /****************************************************
     *长按事件；管理数据。修改和删除功能
     *****************************************************/
    private fun managerDialog(orders: OrderItem) {
        val view = layoutInflater.inflate(R.layout.delete_or_update_dialog_view, null)
        val delete = view.findViewById<Button>(R.id.delete_action)
        delete.text = "删除"
        val update = view.findViewById<Button>(R.id.update_action)
        update.text = "修改"
        val managerDialog = Builder(context)
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

    /**
     * 弹出删除对话框
     */
    private fun deleteDialog(orders: OrderItem) {
        val dialog = Builder(context)
            .setIcon(drawable.ic_alert_name)
            .setTitle("确定要删除吗！！")
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                deleteOrders(orders)
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    /**
     * 修改购物车商品信息，主要是数量和增加备注
     */
    private fun updateDialog(orders: OrderItem) {
        val view = layoutInflater.inflate(layout.edit_goods_of_shoppingcart_dialog_view, null)
        val goodsQuantity = view.findViewById<EditText>(R.id.et_goods_quantity)
        val goodsOfNote = view.findViewById<EditText>(R.id.et_goods_of_note)
        goodsQuantity.setText(orders.quantity.toString())
        goodsOfNote.setText(orders.note)

        val dialog = Builder(context)
            .setIcon(drawable.ic_update_name)
            .setTitle("修改商品信息")
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                val quantity = goodsQuantity.text.toString().trim()
                val note = goodsOfNote.text.toString().trim()
                if (quantity.isEmpty()) {
                    toast { "请填写必须内容！！" }
                } else {
                    val newOrderItem = ObjectQuantityAndNote(quantity.toFloat(), note)
                    viewModel!!.updateOrderItem(newOrderItem, orders.objectId)
                    orders.note = note
                    orders.quantity = quantity.toFloat()
                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }

    //删除订单
    private fun deleteOrders(orders: OrderItem) {
        viewModel!!.deleteOrderItem(orders.objectId)
        viewModel!!.ordersList.remove(orders)
    }


}