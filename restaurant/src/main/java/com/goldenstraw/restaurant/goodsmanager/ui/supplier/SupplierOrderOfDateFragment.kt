package com.goldenstraw.restaurant.goodsmanager.ui.supplier

import android.os.Bundle
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentOrdersOfDateListBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_orders_of_date_list.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 供应商某日订单
 */
class SupplierOrderOfDateFragment : BaseFragment<FragmentOrdersOfDateListBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_supplier_of_order_list
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    private val repository: QueryOrdersRepository by instance()
    var viewModel: QueryOrdersViewModel? = null
    private val orderList = mutableListOf<OrderItem>()
    var adapter: BaseDataBindingAdapter<OrderItem, LayoutOrderItemBinding>? = null
    lateinit var supplier: String
    lateinit var date: String
    override fun initView() {
        super.initView()
        supplier = arguments?.getString("supplier")!!
        date = arguments?.getString("date")!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_order_item,
            dataSource = { orderList },
            dataBinding = { LayoutOrderItemBinding.bind(it) },
            callback = { order, binding, position ->
                binding.orderitem = order
            })


        val where = "{\"\$and\":[{\"supplier\":\"$supplier\"},{\"orderDate\":\"$date\"}]}"
        viewModel!!.getOrdersOfSupplier(where)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe(
                {
                    orderList.clear()
                    orderList.addAll(it)
                    toolbar.title = date + "订单"
                    summation(orderList)
                    adapter!!.forceUpdate()
                }, {}, {})
    }

    /**
     * 计算已记帐订单的合计金额
     */
    private fun summation(orders: MutableList<OrderItem>) {
        val sum = 0.0f
        Observable.fromIterable(orders)
            .scan(sum) { sum, orderItem ->
                sum + orderItem.checkQuantity * orderItem.unitPrice
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe {
                toolbar.subtitle = "共" + orders.size + "项" + "    记帐金额" + it + "元"
            }
    }
}