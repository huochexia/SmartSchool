package com.goldenstraw.restaurant.goodsmanager.ui.query

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityCheckOrdersBinding
import com.goldenstraw.restaurant.databinding.ActivityQueryOrderBinding
import com.goldenstraw.restaurant.goodsmanager.di.queryordersactivitymodule
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * Created by Administrator on 2019/10/23 0023
 */
class QueryOrdersActivity : BaseActivity<ActivityQueryOrderBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_query_order
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(queryordersactivitymodule)
    }

    val repository  by instance<QueryOrdersRepository>()
    var viewModel: QueryOrdersViewModel? = null
    override fun initView() {
        super.initView()
        viewModel = getViewModel {
            QueryOrdersViewModel(repository)
        }

    }

}