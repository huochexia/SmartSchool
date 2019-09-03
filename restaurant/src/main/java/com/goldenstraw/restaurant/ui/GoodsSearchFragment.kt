package com.goldenstraw.restaurant.ui

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentGoodsListBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import org.kodein.di.Copy
import org.kodein.di.Kodein

class GoodsSearchFragment : BaseFragment<FragmentGoodsListBinding>() {
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    override val layoutId: Int
        get() = R.layout.fragment_search_goods
}