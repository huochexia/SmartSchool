package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.os.Bundle
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCookbookNumberListBinding
import com.goldenstraw.restaurant.databinding.LayoutCookbookAndNumberItemBindingImpl
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CookBookViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import kotlinx.android.synthetic.main.fragment_cookbook_number_list.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class StatisticsCookBookNumFragment : BaseFragment<FragmentCookbookNumberListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_cookbook_number_list

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    var startDate = ""
    var endDate = ""
    var category = ""
    var kind = ""

    override fun initView() {
        super.initView()
        arguments?.run {
            startDate = getString("startDate")!!
            endDate = getString("endDate")!!
            category = getString("Category")!!
            kind = getString("Kind")!!

        }
        tv_cookbook_number_toolbar.title = category
        tv_cookbook_number_toolbar.subtitle = "$kind($startDate-$endDate)"
    }

    private val repository by instance<CookBookRepository>()

    lateinit var viewModel: CookBookViewModel

    val adapter: BaseDataBindingAdapter<String, LayoutCookbookAndNumberItemBindingImpl>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.getViewModel {
            CookBookViewModel(repository)
        }

    }
}