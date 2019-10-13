package com.goldenstraw.restaurant.goodsmanager.ui

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityShoppingCartManagerBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.di.shoppingcartdatasource
import com.goldenstraw.restaurant.goodsmanager.repositories.ShoppingCartRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.ShoppingCartMgViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class ShoppingCartManagerActivity : BaseActivity<ActivityShoppingCartManagerBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_shopping_cart_manager
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(shoppingcartdatasource)
    }
    private val repository by instance<ShoppingCartRepository>()
    var viewModel: ShoppingCartMgViewModel? = null
    var adapter: BaseDataBindingAdapter<GoodsOfShoppingCart, LayoutGoodsItemBinding>? = null
    override fun initView() {
        super.initView()
        viewModel = getViewModel {
            ShoppingCartMgViewModel(repository)
        }


    }
}