package com.goldenstraw.restaurant.goodsmanager.ui.record

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentRecordOrderListBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.databinding.PageOfRecordOrdersBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrderItem
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectState
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
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
import java.text.DecimalFormat

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
    val prefs: PrefsHelper by instance()
    var showNumber = 10
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

        showNumber = prefs.showNumber

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(rlw_record_toolbar)
        setHasOptionsMenu(true)
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
                    transRecordState(orderList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .autoDisposable(scopeProvider)
                        .subscribe {
                            viewModel!!.commitRecordState(it).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .autoDisposable(scopeProvider)
                                .subscribe {
                                    orderList.forEach { order ->
                                        order.state = 4
                                    }
                                    orderAdapter.forceUpdate()

                                }
                        }
                }
                ComputeTotalPrice(orderList, binding)
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
     * 计算小计金额
     */
    private fun ComputeTotalPrice(
        orderList: MutableList<OrderItem>,
        binding: PageOfRecordOrdersBinding
    ) {
        val total = 0.0f
        Observable.fromIterable(orderList)
            .scan(total) { sum, order ->
                sum + order.total
            }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe {
                val format = DecimalFormat("0.00")
                binding.totalPrice.text = "${orderList.size}项:${format.format(it)}"
            }
    }

    /**
     * 查询条件：供应商，日期，状态2或3，区域（0或1）
     */
    private fun getOrderItemList() {
        orderList.clear()
        val where =
            "{\"\$and\":[{\"supplier\":\"$supplier\"}" +
                    ",{\"orderDate\":\"$orderDate\"}" +
                    ",{\"state\":{\"\$gte\":3}}" +
                    ",{\"district\":$district}]}"
        viewModel!!.getAllOrderOfDate(where)
            .flatMap {
                Observable.fromIterable(it)
            }
            .buffer(showNumber)
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

    /**
     * 提交记帐
     */
    fun transRecordState(orderList: MutableList<OrderItem>): Observable<BatchOrdersRequest<ObjectState>> {
        return Observable.fromIterable(orderList)
            .map {
                var updateState = ObjectState(4)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_set_buffer, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_number_6 -> {
                showNumber = 6

            }
            R.id.show_number_8 -> {
                showNumber = 8
            }
            R.id.show_number_10 -> {
                showNumber = 10
            }
        }
        prefs.showNumber = showNumber
        getOrderItemList()
        return true
    }
}