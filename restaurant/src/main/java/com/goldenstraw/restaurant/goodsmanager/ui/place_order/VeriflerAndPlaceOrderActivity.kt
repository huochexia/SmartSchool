package com.goldenstraw.restaurant.goodsmanager.ui.place_order

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityPlaceOneOrdersBinding
import com.goldenstraw.restaurant.goodsmanager.di.verifyandplaceorderdatasource
import com.owner.basemodule.base.view.activity.BaseActivity
import org.kodein.di.Copy
import org.kodein.di.Kodein

class VeriflerAndPlaceOrderActivity : BaseActivity<ActivityPlaceOneOrdersBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_place_one_orders
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(verifyandplaceorderdatasource)
    }
}