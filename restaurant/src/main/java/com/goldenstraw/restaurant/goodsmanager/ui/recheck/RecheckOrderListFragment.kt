package com.goldenstraw.restaurant.goodsmanager.ui.recheck

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentRecheckOrderListBinding
import com.goldenstraw.restaurant.databinding.LayoutRecheckItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectCheckGoods
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
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
import kotlinx.android.synthetic.main.fragment_recheck_order_list.*
import kotlinx.android.synthetic.main.fragment_record_order_list.rlw_record_toolbar
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.text.DecimalFormat

/**
 * 列示某供应商的某日验货或记帐的清单
 * Created by Administrator on 2019/10/29 0029
 */
class RecheckOrderListFragment : BaseFragment<FragmentRecheckOrderListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_recheck_order_list

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    private val repository: VerifyAndPlaceOrderRepository by instance()
    var viewModel: VerifyAndPlaceOrderViewModel? = null
    var orderList = mutableListOf<OrderItem>()
    var adapter: BaseDataBindingAdapter<OrderItem, LayoutRecheckItemBinding>? = null

    var supplier = ""
    var orderDate = ""
    var district = 0

    override fun initView() {
        super.initView()
        supplier = arguments?.getString("supplier")!!
        orderDate = arguments?.getString("orderDate")!!
        district = arguments?.getInt("district")!!
        rlw_record_toolbar.title = supplier
        rlw_record_toolbar.subtitle = orderDate

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_recheck_item,
            dataSource = { orderList },
            dataBinding = { LayoutRecheckItemBinding.bind(it) },
            callback = { order, binding, position ->
                binding.orderitem = order
                binding.clickEvent = object : Consumer<OrderItem> {
                    override fun accept(t: OrderItem) {
                        val view = LayoutInflater.from(context).inflate(
                            R.layout.only_input_number_dialog_view,
                            null
                        )
                        val edit = view.findViewById<EditText>(R.id.number_edit)
                        val dialog = AlertDialog.Builder(context)
                            .setTitle("再次确认数量")
                            .setIcon(R.mipmap.add_icon)
                            .setView(view)
                            .setNegativeButton("取消") { dialog, which ->
                                dialog.dismiss()
                            }
                            .setPositiveButton("确定") { dialog, which ->
                                val again = edit.text.toString().trim().toFloat()
                                againQuantity(order, again)
                                dialog.dismiss()
                            }.create()
                        dialog.show()
                    }

                }
            }

        )
        getOrderItemList()
    }


    private fun againQuantity(
        order: OrderItem,
        again: Float
    ) {
        order.againCheckQuantity = again
        val newQuantity = ObjectCheckGoods(
            quantity = order.quantity,
            checkQuantity = order.checkQuantity,
            againCheckQuantity = again,
            total = order.total,
            state = order.state
        )
        viewModel!!.setCheckQuantity(newQuantity, order.objectId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                adapter!!.forceUpdate()
            }, {})
    }

    /**
     * 计算小计金额
     */
    fun computeTotalPrice() {
        val total = 0.0f
        Observable.fromIterable(orderList)
            .scan(total) { sum, order ->
                sum + order.againCheckQuantity * order.unitPrice - order.total
            }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe {
                val format = DecimalFormat("0")
                tv_account_result.text = format.format(it)
            }
    }

    /**
     * 查询条件：供应商，日期，区域（0或1）
     */
    private fun getOrderItemList() {
        orderList.clear()
        val where =
            "{\"\$and\":[{\"supplier\":\"$supplier\"}" +
                    ",{\"orderDate\":\"$orderDate\"}" +
                    ",{\"district\":$district}]}"
        viewModel!!.getAllOrderOfDate(where)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                orderList.addAll(it)
                adapter!!.forceUpdate()
            }, {

            }, {
            })

    }


}