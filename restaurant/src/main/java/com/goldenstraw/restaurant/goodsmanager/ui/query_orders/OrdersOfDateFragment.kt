package com.goldenstraw.restaurant.goodsmanager.ui.query_orders

import android.os.Bundle
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentOrdersOfDateListBinding
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class OrdersOfDateFragment : BaseFragment<FragmentOrdersOfDateListBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_orders_of_date_list
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
    }
    val repository: QueryOrdersRepository by instance()
    var viewModel: QueryOrdersViewModel? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }
    }
}