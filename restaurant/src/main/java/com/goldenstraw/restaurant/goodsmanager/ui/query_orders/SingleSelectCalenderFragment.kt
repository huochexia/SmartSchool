package com.goldenstraw.restaurant.goodsmanager.ui.query_orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import kotlinx.android.synthetic.main.fragment_single_date_select.*
import java.util.*

/**
 * Created by Administrator on 2019/10/23 0023
 */
class SingleSelectCalenderFragment
    : Fragment(), CalendarView.OnCalendarSelectListener {


    protected val layoutId: Int
        get() = R.layout.fragment_single_date_select

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return LayoutInflater.from(context).inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    fun initView() {

        //点击小日历返回当前日期
        fl_current.setOnClickListener { calendarView.scrollToCurrent() }

        calendarView.setOnCalendarSelectListener(this)

        tv_year.text = calendarView.curYear.toString()
        tv_month_day.text = calendarView.curMonth.toString() + "月" + calendarView.curDay + "日"
        tv_current_day.text = calendarView.curDay.toString()
        tv_lunar.text = "今日"
        //
        initData()
    }

    protected fun initData() {
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

    @SuppressLint("SetTextI18n")
    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
        tv_month_day.visibility = View.VISIBLE
        tv_year.visibility = View.VISIBLE
        tv_month_day.text = calendar!!.month.toString() + "月" + calendar.day + "日"
        tv_year.text = calendar!!.year.toString()
        tv_lunar.text = calendar.lunar
        findNavController().navigate(R.id.selectSupplierFragment)
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {
        TODO("not implemented")
    }
}