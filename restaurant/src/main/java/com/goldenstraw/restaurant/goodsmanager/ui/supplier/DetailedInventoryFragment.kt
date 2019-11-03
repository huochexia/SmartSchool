package com.goldenstraw.restaurant.goodsmanager.ui.supplier

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSupplierOfDetailInventoryBinding
import com.goldenstraw.restaurant.databinding.TotalOfOrderDetailBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.utils.mergeSimilarOrderItem
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class DetailedInventoryFragment : BaseFragment<FragmentSupplierOfDetailInventoryBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_supplier_of_detail_inventory

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    private val repository: QueryOrdersRepository by instance()
    var viewModel: QueryOrdersViewModel? = null
    var details = mutableListOf<OrderItem>()
    var adapter: BaseDataBindingAdapter<OrderItem, TotalOfOrderDetailBinding>? = null
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
            layoutId = R.layout.total_of_order_detail,
            dataSource = { details },
            dataBinding = { TotalOfOrderDetailBinding.bind(it) },
            callback = { order, binding, posititon ->
                binding.order = order
                binding.orderPosition.text = "${posititon.plus(1)}."
            }
        )
        getAllOfOrderAndSum()
    }

    /**
     * 获得所有订单
     */
    fun getAllOfOrderAndSum() {
        details.clear()
        val map = HashMap<String, OrderItem>()
        val where =
            "{\"\$and\":[{\"supplier\":\"$supplier\"}" +
                    ",{\"orderDate\":{\"\$gte\":\"$start\",\"\$lte\":\"$end\"}}" +
                    ",{\"state\":{\"\$ne\":-1}}]}"
        viewModel!!.getOrdersOfSupplier(where)
            .flatMap {
                Observable.fromIterable(it)
            }.map {
                mergeSimilarOrderItem(it, map)
            }
            .distinct()
            .map {
                details.add(map[it]!!)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({

            }, {
                viewState.set(MultiStateView.VIEW_STATE_ERROR)
            }, {
                adapter!!.forceUpdate()
                if (details.isEmpty()) {
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                }
            }, {
                viewState.set(MultiStateView.VIEW_STATE_LOADING)
            })
    }

}