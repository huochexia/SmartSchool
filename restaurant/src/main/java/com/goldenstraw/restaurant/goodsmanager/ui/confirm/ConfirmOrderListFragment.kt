package com.goldenstraw.restaurant.goodsmanager.ui.confirm

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentConfirmOrderListBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_confirm_order_list.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance


class ConfirmOrderListFragment : BaseFragment<FragmentConfirmOrderListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_confirm_order_list

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    private val repository by instance<VerifyAndPlaceOrderRepository>()

    var viewModel: VerifyAndPlaceOrderViewModel? = null
    var adapter = BaseDataBindingAdapter(
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

    var supplier = ""
    var orderDate = ""

    var district = 0

    //需要显示的数据列表
    var showList = mutableListOf<OrderItem>()

    override fun initView() {
        super.initView()
        supplier = arguments!!.getString("supplier")!!
        orderDate = arguments!!.getString("orderDate")!!
        district = arguments!!.getInt("district")
        confirm_order_toolbar.title = supplier
        confirm_order_toolbar.subtitle = orderDate
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
        viewModel!!.defUI.refreshEvent.observe(viewLifecycleOwner) {
            adapter.forceUpdate()
        }
        getOrderItemList()
        confirm_btn.setOnClickListener {
            transRecordState(viewModel!!.ordersList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .autoDisposable(scopeProvider)
                .subscribe {
                    viewModel!!.commitRecordState(it).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .autoDisposable(scopeProvider)
                        .subscribe {
                            viewModel!!.ordersList.forEach { order ->
                                order.state = 3
                            }
                            adapter.forceUpdate()
                        }
                }
        }

    }

    /**
     * 提交记帐
     */
    fun transRecordState(orderList: MutableList<OrderItem>): Observable<BatchOrdersRequest<ObjectState>> {
        return Observable.fromIterable(orderList)
            .map {
                val updateState = ObjectState(3)
                val batch = BatchOrderItem(
                    method = "PUT",
                    path = "/1/classes/OrderItem/${it.objectId}",
                    body = updateState
                )
                batch
            }
            .buffer(40)
            .map {
                val commit = BatchOrdersRequest(it)
                commit
            }
    }

    /**
     * 查看状态由菜单控制，2状态已验，3状态已确认，4状态已记帐
     */
     fun getOrderItemList() {
        showList = when (viewModel!!.orderState) {

            2 -> viewModel!!.ordersList.filter {
                it.supplier == supplier && it.state == 2
            } as MutableList

            3 -> viewModel!!.ordersList.filter {
                it.supplier == supplier && it.state > 2
            } as MutableList
            else -> mutableListOf()
        }

        adapter.forceUpdate()

    }

    /**
    管理数据
     */
    private fun managerDialog(orders: OrderItem) {
        val view = layoutInflater.inflate(R.layout.delete_or_update_dialog_view, null)
        val delete = view.findViewById<Button>(R.id.delete_action)
        delete.visibility = View.GONE
        val update = view.findViewById<Button>(R.id.update_action)
        update.text = "重验"
        val managerDialog = android.app.AlertDialog.Builder(context)
            .setView(view)
            .create()
        managerDialog.show()
        update.setOnClickListener {
            updateDialog(orders)
            managerDialog.dismiss()
        }
    }

    private fun updateDialog(orders: OrderItem) {
        if (orders.state == 4) {
            Toast.makeText(context, "已经记帐不能重新验收！！", Toast.LENGTH_SHORT).show()

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


    /**
     * 重验
     */
    private fun cancelChecked(orderItem: OrderItem) {
        val again = ObjectCheckGoods(orderItem.quantity, 0.0f, 0.0f, 0.0f, 1)
        viewModel!!.setCheckQuantity(again, orderItem)

    }

}