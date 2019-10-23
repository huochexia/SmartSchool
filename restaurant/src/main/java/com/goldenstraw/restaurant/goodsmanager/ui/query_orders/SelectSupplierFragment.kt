package com.goldenstraw.restaurant.goodsmanager.ui.query_orders

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSelectSupplierBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import org.kodein.di.Copy
import org.kodein.di.Kodein

/**
 * Created by Administrator on 2019/10/23 0023
 */
class SelectSupplierFragment : BaseFragment<FragmentSelectSupplierBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_select_supplier

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

}