package com.goldenstraw.restaurant.goodsmanager.ui.place_order

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentPlaceOrderBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.verifyandplaceorderdatasource
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
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

/**
 * 订单Fragment，需要传入订单数据。主要用于区分不同区域的订单
 */
class VerifyAndPlaceOrderFragment(
    private val orderList: MutableList<OrderItem>
) : BaseFragment<FragmentPlaceOrderBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_place_order

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(verifyandplaceorderdatasource)
    }

    val repository: VerifyAndPlaceOrderRepository by instance()
    var viewModel: VerifyAndPlaceOrderViewModel? = null
    var adapterItem: BaseDataBindingAdapter<OrderItem, LayoutOrderItemBinding>? = null
    var informationState = ObservableField<Int>()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
        adapterItem = BaseDataBindingAdapter(
            layoutId = R.layout.layout_order_item,
            dataSource = { orderList },
            dataBinding = { LayoutOrderItemBinding.bind(it) },
            callback = { order, binding, position ->
                run {
                    binding.orderitem = order
                    binding.checkEvent = object : Consumer<OrderItem> {
                        override fun accept(t: OrderItem) {
                            t.isSelected = !t.isSelected
                            order.isSelected = t.isSelected
                        }
                    }
                    binding.cbGoods.isChecked = order.isSelected
                }
            }
        )
        if (orderList.isEmpty()) {
            informationState.set(MultiStateView.VIEW_STATE_EMPTY)
        } else {
            informationState.set(MultiStateView.VIEW_STATE_CONTENT)
        }

        viewModel!!.isRefresh.observe(this, Observer {
            if (it)
                sendOrderToSupplier(viewModel!!.selectedSupplier)
        })
    }

    fun popUpSelectSupplierDialog() {
        viewModel!!.isPopUpSupplierDialog.value = true
    }

    /**
     * 将订单发送给对应的供应商，刷新列表
     */

    private fun sendOrderToSupplier(supplier: String) {
        //1、创建一个已选择的列表
        val selectedList = mutableListOf<OrderItem>()
        orderList.forEach {
            if (it.isSelected) {
                it.supplier = supplier
                selectedList.add(it)
            }
        }
        viewModel!!.transOrdersToBatchRequestObject(selectedList, supplier)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({ it ->
                viewModel!!.sendToOrderToSupplier(it).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .autoDisposable(scopeProvider)
                    .subscribe({

                    }, { error ->
                        Toast.makeText(context, "批量修改" + error.message, Toast.LENGTH_SHORT).show()
                    })
            }, { mess ->
                Toast.makeText(context, mess.message, Toast.LENGTH_SHORT).show()
            }, {
                orderList.removeAll(selectedList)
                adapterItem!!.forceUpdate()
            })

    }

}