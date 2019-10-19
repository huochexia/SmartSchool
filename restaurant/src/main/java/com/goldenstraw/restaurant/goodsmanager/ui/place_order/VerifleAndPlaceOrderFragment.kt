package com.goldenstraw.restaurant.goodsmanager.ui.place_order

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentPlaceOrderBinding
import com.goldenstraw.restaurant.goodsmanager.di.verifyandplaceorderdatasource
import com.owner.basemodule.base.view.fragment.BaseFragment
import org.kodein.di.Copy
import org.kodein.di.Kodein

class VerifleAndPlaceOrderFragment : BaseFragment<FragmentPlaceOrderBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_place_order
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(verifyandplaceorderdatasource)
    }
}