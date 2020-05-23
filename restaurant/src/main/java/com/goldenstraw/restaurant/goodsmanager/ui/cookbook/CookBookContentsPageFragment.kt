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
import kotlin.properties.Delegates

class CookBookContentsPageFragment : BaseFragment<FragmentCookbookContentsPageBinding>() {

    var isSelected by Delegates.notNull<Boolean>()

    override val layoutId: Int
        get() = R.layout.fragment_cookbook_contents_page
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    override fun initView() {
        super.initView()
        arguments?.let {
            isSelected = it.getBoolean("isSelected")
        }
        initEvent()
    }

    fun initEvent() {
        val bundle = Bundle()
        bundle.putBoolean("isSelected", isSelected)
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