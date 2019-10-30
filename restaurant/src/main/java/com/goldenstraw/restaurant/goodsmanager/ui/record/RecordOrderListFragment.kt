package com.goldenstraw.restaurant.goodsmanager.ui.record

import android.os.Bundle
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentRecordOrderListBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.databinding.PageOfRecordOrdersBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_record_order_list.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 列示某供应商的某日验货或记帐的清单
 * Created by Administrator on 2019/10/29 0029
 */
class RecordOrderListFragment : BaseFragment<FragmentRecordOrderListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_record_order_list

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    private val repository: VerifyAndPlaceOrderRepository by instance()
    var viewModel: VerifyAndPlaceOrderViewModel? = null
    var orderList = mutableListOf<MutableList<OrderItem>>()
    var vpAdapter: BaseDataBindingAdapter<MutableList<OrderItem>, PageOfRecordOrdersBinding>? = null

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
        vpAdapter = BaseDataBindingAdapter(
            layoutId = R.layout.page_of_record_orders,
            dataSource = { orderList },
            dataBinding = { PageOfRecordOrdersBinding.bind(it) },
            callback = { orderList, binding, position ->
                var orderAdapter = BaseDataBindingAdapter(
                    layoutId = R.layout.layout_order_item,
                    dataBinding = { LayoutOrderItemBinding.bind(it) },
                    dataSource = { orderList },
                    callback = { order, bind, position ->
                        bind.orderitem = order
                    }
                )
                binding.rlwPage.adapter = orderAdapter
                binding.recordCommit.setOnClickListener {

                }
                val total = 0.0f
                Observable.fromIterable(orderList)
                    .scan(total) { t1, t2 ->
                        total + t2.checkQuantity * t2.unitPrice
                    }
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .autoDisposable(scopeProvider)
                    .subscribe {
                        binding.totalPrice.text = "共${orderList.size}项小计:$it"
                    }
            }
        )
        vp_record_order.adapter = vpAdapter

        TabLayoutMediator(tab_layout, vp_record_order) { tab, position ->
            val p = position + 1
            tab.text = "第  $p  组"
        }.attach()

        getOrderItemList()
    }

    /**
     * 查询条件：供应商，日期，状态2或3，区域（0或1）
     */
    private fun getOrderItemList() {
        orderList.clear()
        val where =
            "{\"\$and\":[{\"supplier\":\"$supplier\"},{\"orderDate\":\"$orderDate\"},{\"state\":{\"\$gte\":2}},{\"district\":$district}]}"
        viewModel!!.getAllOrderOfDate(where)
            .flatMap {
                Observable.fromIterable(it)
            }
            .buffer(10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                orderList.add(it)
            }, {

            }, {
                vpAdapter!!.forceUpdate()
            })

    }

}