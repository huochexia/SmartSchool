package com.goldenstraw.restaurant.goodsmanager.ui.check_orders

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityCheckOrdersBinding
import com.goldenstraw.restaurant.goodsmanager.di.verifyandplaceorderdatasource
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.util.TimeConverter
import kotlinx.android.synthetic.main.activity_check_orders.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.util.*

/**
 * 验货
 * Created by Administrator on 2019/10/28 0028
 */
class CheckQuantityActivity : BaseActivity<ActivityCheckOrdersBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_check_orders
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(verifyandplaceorderdatasource)
    }

    private val repository: VerifyAndPlaceOrderRepository by instance()
    lateinit var viewModel: VerifyAndPlaceOrderViewModel
    lateinit var checkDate: String
    override fun initView() {
        super.initView()
        viewModel = getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
        val currday = Calendar.getInstance()
        val before = TimeConverter.getBeforeDay(currday)
        val year = before.get(Calendar.YEAR)
        val month = before.get(Calendar.MONTH) + 1
        val day = before.get(Calendar.DATE)
        checkDate = "$year-$month-$day"  //验货的是前一天的订单。需要这个时间来确定查询哪一天的订单
        check_toolbar.subtitle = checkDate + "的订单"
    }

}