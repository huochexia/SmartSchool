package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCookbookDetailBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_cookbook_detail.*
import org.kodein.di.Copy
import org.kodein.di.Kodein

class CookBookDetailFragment : BaseFragment<FragmentCookbookDetailBinding>() {
    lateinit var cookCategory: String

    override val layoutId: Int
        get() = R.layout.fragment_cookbook_detail
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    override fun initView() {
        super.initView()
        arguments?.let {
            cookCategory = it.getString("cookcategory")
        }
        toolbar.title = cookCategory
        initEvent()
    }

    fun initEvent() {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_cookbook, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_cook -> {
                var bundle = Bundle()
                bundle.putString("cookcategory", cookCategory)
                findNavController().navigate(R.id.inputCookBookFragment, bundle)
            }
        }
        return true
    }
}