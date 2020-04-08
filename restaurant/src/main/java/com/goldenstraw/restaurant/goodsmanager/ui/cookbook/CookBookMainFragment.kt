package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCookBookBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_cook_book.*
import org.kodein.di.Copy
import org.kodein.di.Kodein

class CookBookMainFragment : BaseFragment<FragmentCookBookBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_cook_book
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    override fun initView() {
        super.initView()
        btn_classical_cookbook.setOnClickListener{
            findNavController().navigate(R.id.classicalCookBookFragment)
        }
        btn_daily_meal.setOnClickListener{
            findNavController().navigate(R.id.dailyCookBookFragment)
        }
    }
}