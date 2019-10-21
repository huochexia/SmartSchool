package com.goldenstraw.restaurant.goodsmanager.ui.place_order

import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityVerifyPlaceOrdersBinding
import com.goldenstraw.restaurant.goodsmanager.di.verifyandplaceorderdatasource
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.util.TimeConverter
import com.owner.basemodule.util.toast
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
    private val orderList = mutableListOf<OrderItem>() //区域0的订单

    val state = ObservableField<Int>()
    lateinit var fragment1: VerifyAndPlaceOrderFragment
    lateinit var fragment2: VerifyAndPlaceOrderFragment
    override fun initView() {
        super.initView()
        setSupportActionBar(toolbar)//没有这个显示不了菜单
        viewModel = getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
        getAllOrderOfDate(TimeConverter.getCurrentDateString())

        viewModel!!.isPopUpSupplierDialog.observe(this, Observer {
            if (it)
                popUpSelectSupplierDialog()
        })
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
                state.set(MultiStateView.VIEW_STATE_CONTENT)
            }, {
                toast { it.message.toString() }
                state.set(MultiStateView.VIEW_STATE_ERROR)
            }, {

            }, {
                state.set(MultiStateView.VIEW_STATE_LOADING)
            })
    }

    /**
     * 创建不同订单对应的Fragment
     */
    private fun createFragmentList(orderList: List<OrderItem>) {
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
        val fragments = mutableListOf<VerifyAndPlaceOrderFragment>()
        fragment1 = VerifyAndPlaceOrderFragment(list0)
        fragment2 = VerifyAndPlaceOrderFragment(list1)
        fragments.add(fragment1)
        fragments.add(fragment2)
        mViewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment {
                return fragments[position]
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

    /**
     * 创建供应商单选对话框
     */
    private fun popUpSelectSupplierDialog() {
        val supplierList = mutableListOf<String>()
        viewModel!!.suppliers.forEach {
            supplierList.add(it.username.toString())
        }
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, supplierList
        )
        var supplier = ""
        val dialog = AlertDialog.Builder(this)
            .setIcon(R.mipmap.add_icon)
            .setTitle("请选择供应商")
            .setSingleChoiceItems(adapter, 0) { dialog, which ->
                supplier = supplierList[which]
            }
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                viewModel!!.selectedSupplier = supplier
                viewModel!!.isRefresh.value = true
                dialog.dismiss()
            }.create()
        dialog.show()

    }
}