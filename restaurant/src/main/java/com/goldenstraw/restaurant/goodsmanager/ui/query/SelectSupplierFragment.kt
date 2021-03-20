package com.goldenstraw.restaurant.goodsmanager.ui.query

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.owner.basemodule.network.parserResponse
import com.owner.basemodule.room.entities.User
import kotlinx.android.synthetic.main.fragment_select_supplier.*
import kotlinx.coroutines.launch
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.text.DecimalFormat

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


    val repository by instance<QueryOrdersRepository>()

    var viewModel: QueryOrdersViewModel? = null

    var date = ""
    var adapter = BaseDataBindingAdapter(
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

    @SuppressLint("SetTextI18n")
    override fun initView() {
        super.initView()
        date = arguments?.getString("date")!!
        total_of_current_date.text = "${date}的记帐总额："

        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }

        launch {

            viewModel!!.getAllSupplier()

            val where = "{\"\$and\":[{\"orderDate\":\"$date\"},{\"state\":4}]}"
            parserResponse(viewModel!!.getTotalOfSupplier(where)) {
                if (it.isNotEmpty()) {
                    val format = DecimalFormat(".00")
                    val sum = format.format(it[0]._sumTotal)
                    price_total_of_day.text = "${sum}元"
                } else {
                    price_total_of_day.text = "0.00元"
                }
            }
        }

    }
}