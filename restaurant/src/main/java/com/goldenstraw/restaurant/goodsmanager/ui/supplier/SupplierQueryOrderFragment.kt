package com.goldenstraw.restaurant.goodsmanager.ui.supplier

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSingleDateSelectBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import org.kodein.di.Copy
import org.kodein.di.Kodein

class SupplierQueryOrderFragment : BaseFragment<FragmentSingleDateSelectBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_single_date_select

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
}