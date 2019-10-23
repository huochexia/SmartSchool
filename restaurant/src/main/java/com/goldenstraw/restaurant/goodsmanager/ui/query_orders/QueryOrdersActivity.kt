package com.goldenstraw.restaurant.goodsmanager.ui.query_orders

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityQueryOrderBinding
import com.owner.basemodule.base.view.activity.BaseActivity
import org.kodein.di.Copy
import org.kodein.di.Kodein

/**
 * Created by Administrator on 2019/10/23 0023
 */
class QueryOrdersActivity : BaseActivity<ActivityQueryOrderBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_query_order
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

}