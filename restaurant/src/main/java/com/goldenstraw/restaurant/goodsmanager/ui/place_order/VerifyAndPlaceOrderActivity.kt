package com.goldenstraw.restaurant.goodsmanager.ui.place_order

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityVerifyPlaceOrdersBinding
import com.goldenstraw.restaurant.goodsmanager.di.verifyandplaceorderdatasource
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.util.TimeConverter
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_verify_place_orders.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class VerifyAndPlaceOrderActivity : BaseActivity<ActivityVerifyPlaceOrdersBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_verify_place_orders
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(verifyandplaceorderdatasource)
    }

    val repository: VerifyAndPlaceOrderRepository by instance()
    var viewModel: VerifyAndPlaceOrderViewModel? = null
    val orderList = mutableListOf<OrderItem>() //区域0的订单
    override fun initView() {
        super.initView()
        viewModel = getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
        getAllOrderOfDate(TimeConverter.getCurrentDateString())
    }

    /**
     * 获取订单信息
     * 将来可以按类别进行分组
     */
    private fun getAllOrderOfDate(date: String) {
        viewModel!!.getAllOrderOfDate(date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                orderList.clear()
                orderList.addAll(it)
                createFragmentList(orderList)
            }, {

            }, {

            })
    }

    /**
     * 创建不同订单对应的Fragment
     */
    fun createFragmentList(orderList: List<OrderItem>) {
        val list0 = mutableListOf<OrderItem>()
        val list1 = mutableListOf<OrderItem>()
        orderList.forEach {
            when (it.district) {
                0 -> {
                    list0.add(it)
                }
                1 -> {
                    list1.add(it)
                }
            }
        }
        val fragmentlist = mutableListOf<VerifyAndPlaceOrderFragment>()
        val fragment1 = VerifyAndPlaceOrderFragment(list0)
        val fragment2 = VerifyAndPlaceOrderFragment(list1)
        fragmentlist.add(fragment1)
        fragmentlist.add(fragment2)
        mViewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragmentlist.size
            override fun createFragment(position: Int): Fragment {
                return fragmentlist[position]
            }
        }
        TabLayoutMediator(tab_layout, mViewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "新石南路校区"
                }

                1 -> {
                    tab.text = "西山校区"
                }
            }
        }.attach()
    }
}