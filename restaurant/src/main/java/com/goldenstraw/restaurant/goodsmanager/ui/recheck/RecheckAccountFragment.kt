package com.goldenstraw.restaurant.goodsmanager.ui.recheck


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentRecheckAccountDateBinding
import com.goldenstraw.restaurant.goodsmanager.adapter.SupplierSpinnerAdapter
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.ui.recheck.util.RecheckOrderRepository
import com.goldenstraw.restaurant.goodsmanager.ui.recheck.util.RecheckOrderViewModel
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.room.entities.User
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recheck_account_date.*
import kotlinx.android.synthetic.main.fragment_supplier_account_select.btn_account_orders
import kotlinx.android.synthetic.main.fragment_supplier_account_select.calendarView
import kotlinx.android.synthetic.main.fragment_supplier_account_select.iv_clear
import kotlinx.android.synthetic.main.fragment_supplier_account_select.tv_left_date
import kotlinx.android.synthetic.main.fragment_supplier_account_select.tv_left_week
import kotlinx.android.synthetic.main.fragment_supplier_account_select.tv_month
import kotlinx.android.synthetic.main.fragment_supplier_account_select.tv_right_date
import kotlinx.android.synthetic.main.fragment_supplier_account_select.tv_right_week
import org.jetbrains.anko.bundleOf
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class RecheckAccountFragment : BaseFragment<FragmentRecheckAccountDateBinding>(),
    CalendarView.OnCalendarRangeSelectListener {

    override val layoutId: Int
        get() = R.layout.fragment_recheck_account_date
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    var supplierName = ""
    lateinit var start: String
    lateinit var end: String

    private val repository: RecheckOrderRepository by instance()
    var viewModel: RecheckOrderViewModel? = null
    var adapter: SupplierSpinnerAdapter? = null
    var suppliers = mutableListOf<User>()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            RecheckOrderViewModel(repository)
        }

    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        calendarView.setOnCalendarRangeSelectListener(this)
        tv_month.text = calendarView.curMonth.toString() + "月"
        initEvent()
    }

    private fun initEvent() {
        calendarView.setOnMonthChangeListener { _, month ->
            tv_month.text = month.toString() + "月"
        }

        btn_account_orders.setOnClickListener {
            val calendars = calendarView.selectCalendarRange
            if (calendars == null || calendars.size == 0) {
                Toast.makeText(context, "请选择区间日期！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bundle =Bundle()
            bundle.putString("start",start)
            bundle.putString("end",end)
            findNavController().navigate(R.id.recheckAccountSupplierFragment, bundle)
        }
        iv_clear.setOnClickListener {
            calendarView.clearSelectRange()
            tv_left_week.text = "开始日期"
            tv_right_week.text = "结束日期"
            tv_left_date.text = ""
            tv_right_date.text = ""

        }

    }

    override fun onCalendarSelectOutOfRange(calendar: Calendar) {
        // TODO: 2018/9/13 超出范围提示
    }

    override fun onSelectOutOfRange(calendar: Calendar, isOutOfMinRange: Boolean) {
        Toast.makeText(
            context,
            calendar.toString() + if (isOutOfMinRange) "小于最小选择范围" else "超过最大选择范围",
            Toast.LENGTH_SHORT
        ).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onCalendarRangeSelect(calendar: Calendar, isEnd: Boolean) {
        if (!isEnd) {
            start = calendar.year.toString() + "-" + calendar.month + "-" + calendar.day
            tv_left_date.text = calendar.month.toString() + "月" + calendar.day + "日"
            tv_left_week.text = WEEK[calendar.week]
            tv_right_week.text = "结束日期"
            tv_right_date.text = ""
        } else {
            end = calendar.year.toString() + "-" + calendar.month + "-" + calendar.day
            tv_right_date.text = calendar.month.toString() + "月" + calendar.day + "日"
            tv_right_week.text = WEEK[calendar.week]
        }
    }

    companion object {
        private val WEEK = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
    }


}
