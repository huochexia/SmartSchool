package com.goldenstraw.restaurant.goodsmanager.ui.place_order

import android.os.Bundle
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentPlaceOrderBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderCategoryCardBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.databinding.LayoutShoppingCartItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.verifyandplaceorderdatasource
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import io.reactivex.Observable
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
                        }
                    }
                    binding.cbGoods.isChecked = order.isSelected
                }
            }
        )
    }
}