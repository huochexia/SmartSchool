package com.goldenstraw.restaurant.goodsmanager.ui.place_order

import android.app.AlertDialog
import android.os.Bundle
import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentPlaceOrderBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderCategoryCardBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.databinding.LayoutShoppingCartItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.verifyandplaceorderdatasource
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.util.toast
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
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

    private val _informationState = ObservableField<Int>()

    var informationState = ObservableField<Int>()
        get() = _informationState

    fun setInformationState(state: Int) {
        _informationState.set(state)
    }

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
            setInformationState(MultiStateView.VIEW_STATE_EMPTY)
        } else {
            setInformationState(MultiStateView.VIEW_STATE_CONTENT)
        }
    }

    /**
     * 创建供应商单选对话框
     */
    fun popUpSelectSupplierDialog() {
        val supplierName = arrayListOf<String>()
        viewModel!!.suppliers.forEach {
            supplierName.add(it.username.toString())
        }
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.mipmap.add_icon)
            .setTitle("请选择供应商")
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                sendOrderToSupplier()
                dialog.dismiss()
            }.create()
        dialog.show()

    }

    /**
     * 将订单发送给对应的供应商，刷新列表
     */

    private fun sendOrderToSupplier() {
        //1、创建一个已选择的列表
        val selectedList = mutableListOf<OrderItem>()
        orderList.forEach {
            if (it.isSelected) {
                it.supplier = "张三"
                selectedList.add(it)
            }
        }
        viewModel!!.transOrdersToBatchRequestObject(selectedList).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                viewModel!!.sendToOrderToSupplier(it).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .autoDisposable(scopeProvider)
                    .subscribe({

                    }, {

                    })
            }, {

            }, {
                orderList.removeAll(selectedList)
                adapterItem!!.forceUpdate()
            })

    }

}