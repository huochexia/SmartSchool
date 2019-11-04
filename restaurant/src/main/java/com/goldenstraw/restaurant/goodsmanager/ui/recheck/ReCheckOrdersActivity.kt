package com.goldenstraw.restaurant.goodsmanager.ui.recheck

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityRecheckOrderBinding
import com.goldenstraw.restaurant.goodsmanager.di.verifyandplaceorderdatasource
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.ui.recheck.util.RecheckOrderRepository
import com.goldenstraw.restaurant.goodsmanager.ui.recheck.util.RecheckOrderViewModel
import com.goldenstraw.restaurant.goodsmanager.ui.recheck.util.recheckorderactivitymodule
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * Created by Administrator on 2019/10/29 0029
 */
class ReCheckOrdersActivity : BaseActivity<ActivityRecheckOrderBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_recheck_order

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(recheckorderactivitymodule)
    }
    private val repository: RecheckOrderRepository by instance()
    lateinit var viewModel: RecheckOrderViewModel
    override fun initView() {
        super.initView()
        viewModel = getViewModel {
            RecheckOrderViewModel(repository)
        }
    }
}