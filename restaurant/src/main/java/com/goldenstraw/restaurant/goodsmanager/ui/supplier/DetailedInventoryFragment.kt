package com.goldenstraw.restaurant.goodsmanager.ui.supplier

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSupplierOfDetailInventoryBinding
import com.goldenstraw.restaurant.databinding.GroupByOrderTotalBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.http.entities.SumByGroup
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.text.DecimalFormat

/**
 * 已经记帐订单清单，合并同类商品求和
 */
class DetailedInventoryFragment : BaseFragment<FragmentSupplierOfDetailInventoryBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_supplier_of_detail_inventory

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    private val repository: QueryOrdersRepository by instance()
    var viewModel: QueryOrdersViewModel? = null
    var details = mutableListOf<SumByGroup>()
    var adapter: BaseDataBindingAdapter<SumByGroup, GroupByOrderTotalBinding>? = null
    var totalAllOrder = MutableLiveData<Float>()
    var viewState = ObservableField<Int>()

    var start = ""
    var end = ""
    var supplier = ""
    override fun initView() {
        super.initView()
        start = arguments?.getString("start")!!
        end = arguments?.getString("end")!!
        supplier = arguments?.getString("supplier")!!
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }

        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.group_by_order_total,
            dataSource = { details },
            dataBinding = { GroupByOrderTotalBinding.bind(it) },
            callback = { order, binding, posititon ->
                binding.group = order
                binding.orderPosition.text = "${posititon.plus(1)}."
            }
        )
        getAllOfOrderAndSum()
    }

    /**
     * 获得所有已经记帐的订单
     */
    fun getAllOfOrderAndSum() {
        details.clear()
        val map = HashMap<String, OrderItem>()
        val where =
            "{\"\$and\":[{\"supplier\":\"$supplier\"}" +
                    ",{\"orderDate\":{\"\$gte\":\"$start\",\"\$lte\":\"$end\"}}" +
                    ",{\"state\":3}]}"
        viewModel!!.getTotalGroupByName(where)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                if (it.isEmpty()) {
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                    details.clear()
                    details.addAll(it)
                }
            }, {
                viewState.set(MultiStateView.VIEW_STATE_ERROR)
            }, {
                viewModel!!.getTotalOfSupplier(where)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .autoDisposable(scopeProvider)
                    .subscribe {
                        val format = DecimalFormat(".00")
                        if (it.isNotEmpty()) {
                            val sum = it[0]._sumTotal
                            totalAllOrder.value = format.format(sum).toFloat()
                        }
                    }
            }, {
                viewState.set(MultiStateView.VIEW_STATE_LOADING)
            })
//        viewModel!!.getOrdersOfSupplier(where)
//            .flatMap {
//                Observable.fromIterable(it)
//            }.map {
//                mergeSimilarOrderItem(it, map)
//            }
//            .distinct()
//            .map {
//                details.add(map[it]!!)
//            }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .autoDisposable(scopeProvider)
//            .subscribe({
//
//            }, {
//                viewState.set(MultiStateView.VIEW_STATE_ERROR)
//            }, {
//                showComputerResult()
//            }, {
//                viewState.set(MultiStateView.VIEW_STATE_LOADING)
//            })
    }

    /**
     * 显示最后合并同类项后的计算结果
     */
//    private fun showComputerResult() {
//        adapter!!.forceUpdate()
//        if (details.isEmpty()) {
//            viewState.set(MultiStateView.VIEW_STATE_EMPTY)
//        } else {
//            viewState.set(MultiStateView.VIEW_STATE_CONTENT)
//        }
//        val total = 0.0f
//        Observable.fromIterable(details)
//            .scan(total) { sum, order ->
//                sum + order.total
//            }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .autoDisposable(scopeProvider)
//            .subscribe {
//                val format = DecimalFormat(".00")
//                totalAllOrder.value = format.format(it).toFloat()
//            }
//    }

}