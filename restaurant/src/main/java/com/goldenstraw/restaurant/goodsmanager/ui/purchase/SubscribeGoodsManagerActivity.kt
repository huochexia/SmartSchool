package com.goldenstraw.restaurant.goodsmanager.ui.purchase

import android.content.Intent
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivitySubscribeGoodsBinding
import com.goldenstraw.restaurant.goodsmanager.di.shoppingcartdatasource
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcar.ShoppingCarRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.ShoppingCarMgViewModel
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class SubscribeGoodsManagerActivity:BaseActivity<ActivitySubscribeGoodsBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_subscribe_goods
    override val kodein : Kodein = Kodein.lazy {
        extend(parentKodein,copy = Copy.All)
        import(shoppingcartdatasource)
    }
    private val repository by instance<ShoppingCarRepository>()

    var viewModel: ShoppingCarMgViewModel? = null
    override fun initView() {
        super.initView()
        viewModel = getViewModel{
            ShoppingCarMgViewModel(repository)
        }
    }

    /**
     * 返回当前购物车内数量
     */
    override fun onBackPressed() {
        var intent = Intent()
        intent.putExtra("quantity", viewModel!!.goodsList.size)
        this.setResult(1, intent)
        super.onBackPressed()
    }
}