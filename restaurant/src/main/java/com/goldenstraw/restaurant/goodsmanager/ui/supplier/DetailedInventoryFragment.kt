package com.goldenstraw.restaurant.goodsmanager.ui.supplier

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSupplierOfDetailInventoryBinding
import com.goldenstraw.restaurant.databinding.GroupByOrderTotalBinding
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.network.parserResponse
import kotlinx.coroutines.launch
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.text.DecimalFormat

/**
 * 已经确认的订单，合并同类商品进行求和
 */
class DetailedInventoryFragment : BaseFragment<FragmentSupplierOfDetailInventoryBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_supplier_of_detail_inventory

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    private val repository by instance<QueryOrdersRepository>()

    var viewModel: QueryOrdersViewModel? = null


    @SuppressLint("SetTextI18n")
    val adapter = BaseDataBindingAdapter(
        layoutId = R.layout.group_by_order_total,
        dataSource = { viewModel!!.details },
        dataBinding = { GroupByOrderTotalBinding.bind(it) },
        callback = { order, binding, posititon ->
            binding.group = order
            binding.orderPosition.text = "${posititon.plus(1)}."
        }
    )
    var totalAllOrder = MutableLiveData<Float>()

    var start = ""
    var end = ""
    var supplier = ""


    override fun initView() {
        super.initView()
        start = arguments?.getString("start")!!
        end = arguments?.getString("end")!!
        supplier = arguments?.getString("supplier")!!
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }

        val where =
            "{\"\$and\":[{\"supplier\":\"$supplier\"}" +
                    ",{\"createdAt\":{\"\$gte\":{\"__type\":\"Date\",\"iso\":\"$start\"}}}" +
                    ",{\"createdAt\":{\"\$lte\":{\"__type\":\"Date\",\"iso\":\"$end\"}}}" +
                    ",{\"state\":4}]}"

        viewModel!!.getTotalGroupByName(where)

        viewModel!!.defUI.refreshEvent.observe(viewLifecycleOwner) {
            launch {
                parserResponse(viewModel!!.getTotalOfSupplier(where)) {
                    val format = DecimalFormat(".00")
                    if (it.isNotEmpty()) {
                        val sum = it[0]._sumTotal
                        totalAllOrder.value = format.format(sum).toFloat()
                    }
                }
            }
        }
        viewModel!!.defUI.showDialog.observe(viewLifecycleOwner) {
            AlertDialog.Builder(context!!)
                .setMessage(it)
                .create()
                .show()
        }
    }

}