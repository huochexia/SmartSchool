package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.os.Bundle
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
        var bundle= Bundle()
        btn_classical_cookbook.setOnClickListener{
            bundle.putBoolean("isSelected",false)//用于查看菜谱
            findNavController().navigate(R.id.classicalCookBookFragment,bundle)
        }
        btn_daily_meal.setOnClickListener{
            bundle.putBoolean("isSelected",true)//用于选择菜谱

            findNavController().navigate(R.id.dailyCookBookFragment,bundle)
        }

        btn_analyse_meal.setOnClickListener {


        }
    }
}