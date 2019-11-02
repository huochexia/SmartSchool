package com.goldenstraw.restaurant.goodsmanager.ui.check

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentHaveOrdersOfSupplierBinding
import com.goldenstraw.restaurant.databinding.LayoutSupplierNameItemBinding
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.util.TimeConverter
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_have_orders_of_supplier.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.util.*

/**
 *
 * Created by Administrator on 2019/10/28 0028
 */
class HaveOrdersOfSupplierListFragment : BaseFragment<FragmentHaveOrdersOfSupplierBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_have_orders_of_supplier
    override val kodein: Kodein = Kodein.lazy {

        extend(parentKodein, copy = Copy.All)
    }
    private val prefs: PrefsHelper by instance()
    private val repository: VerifyAndPlaceOrderRepository by instance()
    lateinit var viewModel: VerifyAndPlaceOrderViewModel
    var adapter: BaseDataBindingAdapter<String, LayoutSupplierNameItemBinding>? = null

    lateinit var checkDate: String
    val supplierList = mutableListOf<String>()
    var supplierState = ObservableField<Int>() //显示状态
    var orderState = 1

    override fun initView() {
        super.initView()
        val currday = Calendar.getInstance()
        val before = TimeConverter.getBeforeDay(currday)
        val year = before.get(Calendar.YEAR)
        val month = before.get(Calendar.MONTH) + 1
        val day = before.get(Calendar.DATE)
        checkDate = arguments?.getString("orderDate")!! //验货的是前一天的订单。需要这个时间来确定查询哪一天的订单
        check_toolbar.subtitle = checkDate + "的订单"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(check_toolbar)
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
                        bundle.putString("orderDate", checkDate)
                        bundle.putInt("orderState", orderState)
                        bundle.putInt("district", prefs.district)
                        findNavController().navigate(R.id.checkOrderList, bundle)
                    }
                }
            }
        )
        /*
         区域值应从本地数据中获取，为当前登录用户所有区域值。初始默认是未验状态
         */
        getSupplierListFromWhere(checkDate, orderState, prefs.district)

    }

    /**
     *  获取有订单的供应商名单,状态为1，区域0或1
     */
    private fun getSupplierListFromWhere(date: String, stauts: Int, district: Int) {
        when (stauts) {
            1 -> check_toolbar.title = "供应商列表--未验"
            2 -> check_toolbar.title = "供应商列表--验收"
        }
        supplierList.clear()
        val where =
            "{\"\$and\":[{\"orderDate\":\"$date\"},{\"state\":$stauts},{\"district\":$district}]}"
        viewModel!!.getAllOrderOfDate(where)
            .flatMap {
                Observable.fromIterable(it)
            }
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
        inflater.inflate(R.menu.menu_check_orders, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_checked_order -> {
                check_toolbar.title = "供应商--已验"
                orderState = 2
            }
            R.id.menu_no_check_order -> {
                check_toolbar.title = "供应商--未验"
                orderState = 1
            }
        }
        getSupplierListFromWhere(checkDate, orderState, prefs.district)
        return true

    }

}