package com.goldenstraw.restaurant.goodsmanager.ui.query

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSelectSupplierBinding
import com.goldenstraw.restaurant.databinding.LayoutSupplierItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.queryordersactivitymodule
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.User
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * Created by Administrator on 2019/10/23 0023
 */
class SelectSupplierFragment : BaseFragment<FragmentSelectSupplierBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_select_supplier
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(queryordersactivitymodule)
    }


    val repository: QueryOrdersRepository by instance()

    var viewModel: QueryOrdersViewModel? = null

    var adapter: BaseDataBindingAdapter<User, LayoutSupplierItemBinding>? = null

    override fun initView() {
        super.initView()
        val date = arguments?.getString("date")

        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_supplier_item,
            dataBinding = { LayoutSupplierItemBinding.bind(it) },
            dataSource = { viewModel!!.suppliers },
            callback = { user, binding, position ->
                binding.supplier = user
                binding.clickEvent = object : Consumer<User> {
                    override fun accept(t: User) {
                        val bundle = Bundle()
                        bundle.putString("date", date)
                        bundle.putString("supplier", user.username)
                        findNavController().navigate(R.id.ordersOfDateFragment, bundle)
                    }

                }
            }
        )
    }
}