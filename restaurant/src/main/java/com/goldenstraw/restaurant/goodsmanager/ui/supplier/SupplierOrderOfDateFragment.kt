package com.goldenstraw.restaurant.goodsmanager.ui.supplier

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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
import java.text.DecimalFormat

/**
 * 供应商查看其某日订单情况
 */
class SupplierOrderOfDateFragment : BaseFragment<FragmentOrdersOfDateListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_supplier_of_order_list

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    private val repository by instance<QueryOrdersRepository>()

    var viewModel: QueryOrdersViewModel? = null

    val adapter = BaseDataBindingAdapter(
        layoutId = R.layout.layout_order_item,
        dataSource = { viewModel!!.ordersList },
        dataBinding = { LayoutOrderItemBinding.bind(it) },
        callback = { order, binding, _ ->
            binding.orderitem = order
        })

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

        viewModel!!.defUI.refreshEvent.observe(viewLifecycleOwner) {

            viewModel!!.ordersList.let {
                if (it.size != 0) {
                    when (it[0].district) {
                        0 -> toolbar.title = date + "订单----新石南路校区"
                        1 -> toolbar.title = date + "订单----西山校区"
                    }
                    summation(it)
                }

            }
            adapter.forceUpdate()
        }
        viewModel!!.defUI.showDialog.observe(viewLifecycleOwner) {
            AlertDialog.Builder(context!!)
                .setMessage(it)
                .create()
                .show()
        }
        getOrderOfAll()
    }

    /**
     * 获取某个供应商某日的，数量不为0的所有订单
     */
    private fun getOrderOfAll() {
        val where = "{\"\$and\":[{\"supplier\":\"$supplier\"}" +
                ",{\"orderDate\":\"$date\"}" +
                ",{\"quantity\":{\"\$ne\":0}}]}"

        viewModel!!.getAllOfOrders(where)

    }

    /**
     * 计算已确认订单的合计金额
     */
    private fun summation(orders: MutableList<OrderItem>) {
        val sum = 0.0f
        val format = DecimalFormat(".00")
        Observable.fromIterable(orders)
            .filter {
                it.state >= 3
            }
            .scan(sum) { sum1, orderItem ->
                sum1 + orderItem.total
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe {
                toolbar.subtitle = "共${orders.size}项   确认 ${format.format(it)}元"
            }
    }
}