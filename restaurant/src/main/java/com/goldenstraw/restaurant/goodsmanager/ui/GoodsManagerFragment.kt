package com.goldenstraw.restaurant.goodsmanager.ui

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentGoodsListBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import org.kodein.di.Copy
import org.kodein.di.Kodein

class GoodsManagerFragment : BaseFragment<FragmentGoodsListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_goods_list

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }



}