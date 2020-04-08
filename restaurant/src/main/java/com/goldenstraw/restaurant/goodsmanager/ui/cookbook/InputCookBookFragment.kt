package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentInputCookBookBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_cookbook_detail.*
import org.kodein.di.Copy
import org.kodein.di.Kodein

class InputCookBookFragment : BaseFragment<FragmentInputCookBookBinding>() {

    lateinit var cookCategory: String

    override val layoutId: Int
        get() = R.layout.fragment_input_cook_book

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    override fun initView() {
        super.initView()
        arguments?.let {
            cookCategory = it.getString("cookcategory")
        }

        toolbar.title = cookCategory
    }

}