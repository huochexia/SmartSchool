package com.goldenstraw.restaurant.goodsmanager.ui.query

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentOrdersOfDateListBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.network.parserResponse
import kotlinx.android.synthetic.main.fragment_orders_of_date_list.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.text.DecimalFormat

class OrdersOfDateFragment : BaseFragment<FragmentOrdersOfDateListBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_orders_of_date_list
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
    }

    val repository by instance<QueryOrdersRepository>()

    var viewModel: QueryOrdersViewModel? = null

    val adapter = BaseDataBindingAdapter(
        layoutId = R.layout.layout_order_item,
        dataSource = { viewModel!!.ordersList },
        dataBinding = { LayoutOrderItemBinding.bind(it) },
        callback = { order, binding, position ->
            binding.orderitem = order
            binding.longClick = object : Consumer<OrderItem> {
                override fun accept(t: OrderItem) {
                    managerDialog(t)
                }
            }
        }

    )

    lateinit var supplier: String

    lateinit var date: String

    override fun initView() {
        super.initView()
        supplier = arguments?.getString("supplier")!!
        date = arguments?.getString("date")!!

        toolbar.title = supplier
        toolbar.subtitle = date

    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        total_of_current_date_supplier.text = "${date}的记帐金额："
        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }

        viewModel!!.defUI.refreshEvent.observe(viewLifecycleOwner) {
            adapter.forceUpdate()
        }
        viewModel!!.defUI.showDialog.observe(viewLifecycleOwner) {
            androidx.appcompat.app.AlertDialog.Builder(context!!)
                .setMessage(it)
                .create()
                .show()
        }
        val where = "{\"\$and\":[{\"supplier\":\"$supplier\"},{\"orderDate\":\"$date\"}]}"

        viewModel!!.getAllOfOrders(where)

        launch {
            parserResponse(viewModel!!.getTotalOfSupplier(where)) {
                if (it.isNotEmpty()) {
                    val format = DecimalFormat(".00")
                    val sum = format.format(it[0]._sumTotal)
                    price_total_of_day_supplier.text = "${sum}元"
                } else {
                    price_total_of_day_supplier.text = "0.0元"
                }

            }
        }

    }

    /****************************************************
     *长按事件；管理数据。修改和删除功能
     *****************************************************/
    private fun managerDialog(orders: OrderItem) {
        val view = layoutInflater.inflate(R.layout.delete_or_update_dialog_view, null)
        val delete = view.findViewById<Button>(R.id.delete_action)
        delete.text = "重新发送订单"
        val update = view.findViewById<Button>(R.id.update_action)
        update.visibility = View.GONE
        val managerDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        managerDialog.show()
        delete.setOnClickListener {
            if (orders.state != 1) {
                Toast.makeText(context, "该商品已验收，不能删除！！", Toast.LENGTH_SHORT).show()
            } else {
                deleteDialog(orders)
            }
            managerDialog.dismiss()
        }

    }


    /**
     * 删除对话框
     */

    private fun deleteDialog(order: OrderItem) {
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_alert_name)
            .setTitle("确定重新发送")
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                //将商品信息中的供应商清空，同时state设定为0，则该商品为订货最初状态
                val newOrder = ObjectSupplier("", 0)
                viewModel!!.updateOrderOfSupplier(newOrder, order.objectId)

                dialog.dismiss()
            }.create()
        dialog.show()
    }
}