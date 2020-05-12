package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCookbookContentsPageBinding
import com.goldenstraw.restaurant.goodsmanager.utils.CookKind
import com.owner.basemodule.base.view.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_cookbook_contents_page.*
import org.kodein.di.Copy
import org.kodein.di.Kodein

class CookBookContentsPageFragment : BaseFragment<FragmentCookbookContentsPageBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_cookbook_contents_page
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    override fun initView() {
        super.initView()
        initEvent()
    }

    fun initEvent() {
        val bundle = Bundle()
        tv_cool_food.setOnClickListener {
            bundle.putString("cookcategory", CookKind.ColdFood.kindName)
            findNavController().navigate(R.id.cookBookDetailFragment, bundle)
        }
        tv_hot_food.setOnClickListener {
            bundle.putString("cookcategory", CookKind.HotFood.kindName)
            findNavController().navigate(R.id.cookBookDetailFragment,bundle)
        }
        tv_flour_food.setOnClickListener {
            bundle.putString("cookcategory", CookKind.FlourFood.kindName)
            findNavController().navigate(R.id.cookBookDetailFragment,bundle)
        }
        tv_soup_porri.setOnClickListener {
            bundle.putString("cookcategory", CookKind.SoutPorri.kindName)
            findNavController().navigate(R.id.cookBookDetailFragment,bundle)
        }
        tv_snack_detail.setOnClickListener {
            bundle.putString("cookcategory", CookKind.Snackdetail.kindName)
            findNavController().navigate(R.id.cookBookDetailFragment,bundle)
        }

    }
}