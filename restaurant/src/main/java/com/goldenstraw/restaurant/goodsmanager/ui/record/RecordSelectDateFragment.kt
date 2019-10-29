package com.goldenstraw.restaurant.goodsmanager.ui.record

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentRecordSelectDateBinding
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import kotlinx.android.synthetic.main.fragment_single_date_select.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * Created by Administrator on 2019/10/29 0029
 */
class RecordSelectDateFragment : BaseFragment<FragmentRecordSelectDateBinding>(),
    CalendarView.OnCalendarSelectListener {


    override val layoutId: Int
        get() = R.layout.fragment_record_select_date

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
    }
    private val repository: VerifyAndPlaceOrderRepository by instance()
    var viewModel: VerifyAndPlaceOrderViewModel? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        //点击小日历返回当前日期
        fl_current.setOnClickListener { calendarView.scrollToCurrent() }
        calendarView.setOnCalendarSelectListener(this)
        tv_year.text = calendarView.curYear.toString()
        tv_month_day.text = calendarView.curMonth.toString() + "月" + calendarView.curDay + "日"
        tv_current_day.text = calendarView.curDay.toString()
        tv_lunar.text = "今日"
        initData()
    }

    private fun initData() {
        val year = calendarView.curYear
        val month = calendarView.curMonth
        val map = HashMap<String, Calendar>()
        map[getSchemeCalendar(year, month, 3, -0x40db25, "进货").toString()] =
            getSchemeCalendar(year, month, 3, -0x40db25, "进货")
        map[getSchemeCalendar(year, month, 6, -0x196ec8, "验货").toString()] =
            getSchemeCalendar(year, month, 6, -0x196ec8, "验货")
        map[getSchemeCalendar(year, month, 9, -0x20ecaa, "确认").toString()] =
            getSchemeCalendar(year, month, 9, -0x20ecaa, "确认")
        map[getSchemeCalendar(year, month, 13, -0x123a93, "录入").toString()] =
            getSchemeCalendar(year, month, 13, -0x123a93, "录入")

        calendarView.setSchemeDate(map)

    }

    private fun getCalendar(year: Int, month: Int, day: Int): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        return calendar
    }

    private fun getSchemeCalendar(
        year: Int,
        month: Int,
        day: Int,
        color: Int,
        text: String
    ): Calendar {
        val calendar = getCalendar(year, month, day)
        calendar.schemeColor = color//如果单独标记颜色、则会使用这个颜色
        calendar.scheme = text
        return calendar
    }

    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
        tv_month_day.visibility = View.VISIBLE
        tv_year.visibility = View.VISIBLE
        tv_month_day.text = calendar!!.month.toString() + "月" + calendar.day + "日"
        tv_year.text = calendar!!.year.toString()
        tv_lunar.text = calendar.lunar
        if (isClick) {
            val bundle = Bundle()
            val date = calendar!!.year.toString() + "-" + calendar!!.month + "-" + calendar!!.day
            bundle.putString("orderDate", date)
            findNavController().navigate(R.id.recordSelectSupplierFragment, bundle)
        }
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}