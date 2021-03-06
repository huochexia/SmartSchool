package com.goldenstraw.restaurant.goodsmanager.ui.record

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityRecordOrderBinding
import com.goldenstraw.restaurant.goodsmanager.di.verifyandplaceorderdatasource
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * Created by Administrator on 2019/10/29 0029
 */
class RecordOrdersActivity : BaseActivity<ActivityRecordOrderBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_record_order

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(verifyandplaceorderdatasource)
    }
    private val repository: VerifyAndPlaceOrderRepository by instance()
    lateinit var viewModel: VerifyAndPlaceOrderViewModel
    override fun initView() {
        super.initView()
        viewModel = getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
    }
}