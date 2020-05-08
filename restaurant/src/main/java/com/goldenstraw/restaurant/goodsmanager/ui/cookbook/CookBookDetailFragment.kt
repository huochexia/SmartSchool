package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCookbookDetailBinding
import com.goldenstraw.restaurant.databinding.LayoutCoolbookItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.CookBook
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CookBookViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import kotlinx.android.synthetic.main.fragment_cookbook_detail.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class CookBookDetailFragment : BaseFragment<FragmentCookbookDetailBinding>() {
    lateinit var cookCategory: String

    override val layoutId: Int
        get() = R.layout.fragment_cookbook_detail
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    private val respository by instance<CookBookRepository>()
    lateinit var viewModel: CookBookViewModel
    var adapter: BaseDataBindingAdapter<CookBook, LayoutCoolbookItemBinding>? = null

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

        viewModel = activity!!.getViewModel {

            CookBookViewModel(respository)

        }


        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_coolbook_item,
            dataSource = { viewModel.cookbookList },
            dataBinding = { LayoutCoolbookItemBinding.bind(it) },
            callback = { cookbook, binding, _ ->
                binding.cookbook = cookbook
            }
        )
        viewModel.getCookBookOfCategory(cookCategory)

        viewModel.defUI.refreshEvent.observe(viewLifecycleOwner) {
            adapter!!.forceUpdate()
        }
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