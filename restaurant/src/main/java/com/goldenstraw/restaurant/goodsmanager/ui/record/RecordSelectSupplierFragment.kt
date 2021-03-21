package com.goldenstraw.restaurant.goodsmanager.ui.record

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentRecordSelectSupplierBinding
import com.goldenstraw.restaurant.databinding.LayoutSupplierNameItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_record_select_supplier.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 需要
 * Created by Administrator on 2019/10/29 0029
 */
class RecordSelectSupplierFragment : BaseFragment<FragmentRecordSelectSupplierBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_record_select_supplier

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    private val repository: VerifyAndPlaceOrderRepository by instance()

    var viewModel: VerifyAndPlaceOrderViewModel? = null

    var adapter = BaseDataBindingAdapter(
        layoutId = R.layout.layout_supplier_name_item,
        dataBinding = { LayoutSupplierNameItemBinding.bind(it) },
        dataSource = { supplierList },
        callback = { supplier, binding, position ->
            binding.supplier = supplier
            binding.clickEvent = object : Consumer<String> {
                override fun accept(t: String) {
                    val bundle = Bundle()
                    bundle.putString("supplier", supplier)
                    bundle.putString("orderDate", orderDate)
                    bundle.putInt("district", district)
                    findNavController().navigate(R.id.recordOrderListFragment, bundle)
                }
            }
        }
    )
    var district = 0
    var supplierState = ObservableField<Int>()

    var supplierList = mutableListOf<String>()

    lateinit var orderDate: String
    override fun initView() {
        super.initView()
        orderDate = arguments?.getString("orderDate")!!
        district = arguments?.getInt("district")!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(record_select_toolbar)
        setHasOptionsMenu(true)
        when (district) {
            0 -> record_select_toolbar.title = "新石校区"
            1 -> record_select_toolbar.title = "西山校区"
        }
        record_select_toolbar.subtitle = orderDate

        viewModel = activity!!.getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
        val where =
            "{\"\$and\":[{\"orderDate\":\"${orderDate}\"},{\"district\":${district}}]}"
        viewModel!!.getOrdersOfCondition(where)

        viewModel!!.defUI.refreshEvent.observe(viewLifecycleOwner) {
            val list = viewModel!!.ordersList.filter {
                it.state >= 3
            } as MutableList
            getSupplierListFromWhere(list)
        }
        viewModel!!.defUI.showDialog.observe(viewLifecycleOwner) {
            AlertDialog.Builder(context!!)
                .setMessage(it)
                .create()
                .show()
        }
    }

    /**
     *  获取有订单的供应商名单,状态大于等于3（即确认或记帐），区域0或1
     */
    private fun getSupplierListFromWhere(list: MutableList<OrderItem>) {
        supplierList.clear()
        Observable.fromIterable(list)
            .map {
                it.supplier
            }
            .distinct()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({ supplier ->
                supplier?.let {
                    supplierList.add(it)
                }
            }, {
                supplierState.set(MultiStateView.VIEW_STATE_ERROR)
            }, {
                if (supplierList.isNotEmpty())
                    supplierState.set(MultiStateView.VIEW_STATE_CONTENT)
                else
                    supplierState.set(MultiStateView.VIEW_STATE_EMPTY)
                adapter.forceUpdate()
            })
    }

}