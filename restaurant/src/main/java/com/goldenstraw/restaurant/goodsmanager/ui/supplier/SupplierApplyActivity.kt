package com.goldenstraw.restaurant.goodsmanager.ui.supplier

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivitySupplierApplyBinding
import com.goldenstraw.restaurant.goodsmanager.di.queryordersactivitymodule
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class SupplierApplyActivity : BaseActivity<ActivitySupplierApplyBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_supplier_apply

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(queryordersactivitymodule)
    }

    private val prefs: PrefsHelper by instance()
    private val repository: QueryOrdersRepository by instance()
    var viewModel: QueryOrdersViewModel? = null

    override fun initView() {
        super.initView()
        viewModel = getViewModel {
            QueryOrdersViewModel(repository)
        }
        viewModel!!.supplier = prefs.username
    }

}