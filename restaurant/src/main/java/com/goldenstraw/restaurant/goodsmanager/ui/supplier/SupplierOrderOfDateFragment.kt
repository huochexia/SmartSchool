package com.goldenstraw.restaurant.goodsmanager.ui.supplier

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentOrdersOfDateListBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import org.kodein.di.Copy
import org.kodein.di.Kodein

/**
 * 供应商某日订单
 */
class SupplierOrderOfDateFragment : BaseFragment<FragmentOrdersOfDateListBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_orders_of_date_list
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
}