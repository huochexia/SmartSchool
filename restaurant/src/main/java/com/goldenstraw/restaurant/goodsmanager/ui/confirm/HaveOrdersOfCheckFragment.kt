package com.goldenstraw.restaurant.goodsmanager.ui.confirm

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentHaveOrdersOfConfirmBinding
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
import kotlinx.android.synthetic.main.fragment_have_orders_of_confirm.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 *
 * Created by Administrator on 2019/10/28 0028
 */
class HaveOrdersOfCheckFragment : BaseFragment<FragmentHaveOrdersOfConfirmBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_have_orders_of_confirm
    override val kodein: Kodein = Kodein.lazy {

        extend(parentKodein, copy = Copy.All)
    }

    private val repository by instance<VerifyAndPlaceOrderRepository>()
    lateinit var viewModel: VerifyAndPlaceOrderViewModel
    var adapter: BaseDataBindingAdapter<String, LayoutSupplierNameItemBinding>? = null

    lateinit var orderDate: String
    var district: Int = -1
    val supplierList = mutableListOf<String>()
    var supplierState = ObservableField<Int>() //显示状态
    var orderState = 2

    override fun initView() {
        super.initView()
        orderDate = arguments?.getString("orderDate")!!
        district = arguments?.getInt("district")!!
        confirm_toolbar.subtitle = orderDate + "的订单"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(confirm_toolbar)
        setHasOptionsMenu(true)

        viewModel = activity!!.getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }

        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_supplier_name_item,
            dataSource = { supplierList },
            dataBinding = { LayoutSupplierNameItemBinding.bind(it) },
            callback = { supplier, binding, position ->
                binding.supplier = supplier
                binding.clickEvent = object : Consumer<String> {
                    override fun accept(t: String) {
                        val bundle = Bundle()
                        bundle.putString("supplier", supplier)
                        bundle.putString("orderDate", orderDate)
                        bundle.putInt("orderState", viewModel.orderState)
                        bundle.putInt("district", district)
                        findNavController().navigate(R.id.confirmOrderListFragment, bundle)
                    }
                }
            }
        )
        /*
         *获取某日订单数据，需要结合区域
         */
        val where =
            "{\"\$and\":[{\"orderDate\":\"${orderDate}\"},{\"district\":${district}}]}"
        viewModel.getOrdersOfCondition(where)

        viewModel.defUI.refreshEvent.observe(viewLifecycleOwner) {
            val list = when (viewModel.orderState) {
                2 -> viewModel.ordersList.filter {
                    it.state == viewModel.orderState
                } as MutableList
                3 -> viewModel.ordersList.filter {
                    it.state != viewModel.orderState
                } as MutableList
                else-> mutableListOf()
            }
            getSupplierListFromWhere(list)
        }

    }

    /**
     *  获取有订单的供应商名单,状态为2或3，区域0或1
     */
    private fun getSupplierListFromWhere(list: MutableList<OrderItem>) {

        supplierList.clear()
        //从共享数据中过滤自己需要的数据
        //从过滤的订单列表中，对供应商信息提取
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
                adapter!!.forceUpdate()
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_confirm_orders, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       val filter = when (item.itemId) {
            R.id.menu_confirmed_order -> {
                confirm_toolbar.title = "供应商--已定"
                viewModel.orderState = 3
                viewModel.ordersList.filter {
                    it.state != 2
                } as MutableList
            }
            R.id.menu_no_confirm_order -> {
                confirm_toolbar.title = "供应商--未定"
                viewModel.orderState = 2
                viewModel.ordersList.filter {
                    it.state == 2
                } as MutableList
            }
           else-> mutableListOf()
        }
        getSupplierListFromWhere(filter)
        return true

    }

}