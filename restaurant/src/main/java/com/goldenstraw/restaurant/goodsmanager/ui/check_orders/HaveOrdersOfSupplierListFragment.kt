package com.goldenstraw.restaurant.goodsmanager.ui.check_orders

import android.os.Bundle
import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentHaveOrdersOfSupplierBinding
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * Created by Administrator on 2019/10/28 0028
 */
class HaveOrdersOfSupplierListFragment : BaseFragment<FragmentHaveOrdersOfSupplierBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_have_orders_of_supplier
    override val kodein: Kodein = Kodein.lazy {

        extend(parentKodein, copy = Copy.All)
    }

    private val repository: VerifyAndPlaceOrderRepository by instance()
    lateinit var viewModel: VerifyAndPlaceOrderViewModel

    var supplierState = ObservableField<Int>() //显示状态

    private var orderState: Int? = null  //订单状态


    override fun initView() {
        super.initView()
        orderState = arguments?.getInt("orderState")!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
    }

}