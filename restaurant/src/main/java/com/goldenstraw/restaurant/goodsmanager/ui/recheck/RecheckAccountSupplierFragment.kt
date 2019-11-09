package com.goldenstraw.restaurant.goodsmanager.ui.recheck

import android.app.AlertDialog
import android.os.Bundle
import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentRecheckAccountSupplierBinding
import com.goldenstraw.restaurant.databinding.LayoutRecheckAccountSupplierItemBinding
import com.goldenstraw.restaurant.goodsmanager.ui.recheck.util.RecheckOrderRepository
import com.goldenstraw.restaurant.goodsmanager.ui.recheck.util.RecheckOrderViewModel
import com.goldenstraw.restaurant.goodsmanager.ui.recheck.util.SupplierOfTotal
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class RecheckAccountSupplierFragment : BaseFragment<FragmentRecheckAccountSupplierBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_recheck_account_supplier
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    private val repository: RecheckOrderRepository by instance()
    var viewModel: RecheckOrderViewModel? = null
    var adapter: BaseDataBindingAdapter<SupplierOfTotal, LayoutRecheckAccountSupplierItemBinding>? =
        null
    var suppliers = mutableListOf<SupplierOfTotal>()
    var state = ObservableField<Int>()
    lateinit var start: String
    lateinit var end: String

    override fun initView() {
        super.initView()
        start = arguments?.getString("start")!!
        end = arguments?.getString("end")!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.getViewModel {
            RecheckOrderViewModel(repository)
        }

        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_recheck_account_supplier_item,
            dataBinding = { LayoutRecheckAccountSupplierItemBinding.bind(it) },
            dataSource = { suppliers },
            callback = { supplier, binding, position ->
                binding.supplier = supplier
                binding.clickEvent = object : Consumer<SupplierOfTotal> {
                    override fun accept(t: SupplierOfTotal) {
                        popUpDialog(t)
                    }
                }
            }
        )
        getTotalGroupBySupplier()
    }

    /**
     * 分组求和
     */
    fun getTotalGroupBySupplier() {
        val where =
            "{\"\$and\":[{\"state\":3},{\"orderDate\":{\"\$gte\":\"$start\",\"\$lte\":\"$end\"}}]}"
        viewModel!!.getTotalOfSuppliers(where)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                if (it.isEmpty()) {
                    state.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    state.set(MultiStateView.VIEW_STATE_CONTENT)

                    suppliers.clear()
                    suppliers.addAll(it)
                    adapter!!.forceUpdate()
                }
            }, {
                state.set(MultiStateView.VIEW_STATE_ERROR)
            }, {}, {
                state.set(MultiStateView.VIEW_STATE_LOADING)
            })
    }

    /**
     * 弹出合并数据对话框
     */
    fun popUpDialog(supplier: SupplierOfTotal) {
        val dialog = AlertDialog.Builder(context)
            .setTitle("确定要合并数据吗？")
            .setIcon(R.mipmap.add_icon)
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                combineQuantity(supplier.supplier, start, end)
                dialog.dismiss()
            }.create()
        dialog.show()

    }

    private fun combineQuantity(supplier: String, start: String, end: String) {
        val where =
            "{\"\$and\":[{\"supplier\":\"$supplier\"},{\"orderDate\":{\"\$gte\":\"$start\",\"\$lte\":\"$end\"}}" +
                    ",{\"state\":3}]}"
        viewModel!!.getAllOrderOfCondition(where)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({ list ->
                viewModel!!.transOrdersToBatchRequestObject(list)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .autoDisposable(scopeProvider)
                    .subscribe { batch ->
                        viewModel!!.checkQuantityOfOrders(batch)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .autoDisposable(scopeProvider)
                            .subscribe({
                                getTotalGroupBySupplier()
                            }, {})
                    }
            }, {

            }, {

            })


    }

}