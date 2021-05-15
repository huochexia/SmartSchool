package com.goldenstraw.restaurant.goodsmanager.ui.confirm

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityConfirmOrdersBinding
import com.goldenstraw.restaurant.goodsmanager.di.verifyandplaceorderdatasource
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 验货
 * Created by Administrator on 2019/10/28 0028
 */
class ConfirmQuantityActivity : BaseActivity<ActivityConfirmOrdersBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_confirm_orders
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(verifyandplaceorderdatasource)
    }

    private val repository  by instance<VerifyAndPlaceOrderRepository>()
    lateinit var viewModel: VerifyAndPlaceOrderViewModel
    override fun initView() {
        super.initView()

        viewModel = getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }



    }

}