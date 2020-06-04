package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentAnalyesMealSelectDateBinding
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.owner.basemodule.base.view.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_analyes_meal_select_date.*
import org.kodein.di.Copy
import org.kodein.di.Kodein

class CookBookanalyesFragment : BaseFragment<FragmentAnalyesMealSelectDateBinding>()
    , CalendarView.OnCalendarRangeSelectListener {
    override val layoutId: Int
        get() = R.layout.fragment_analyes_meal_select_date

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    lateinit var start: String
    lateinit var end: String


    override fun initView() {
        calendarView.setOnCalendarRangeSelectListener(this)

        tv_month.text = calendarView.curMonth.toString() + "月"

        tv_current_day.text = calendarView.curDay.toString()

        initEvent()
    }

    fun initEvent() {
        calendarView.setOnMonthChangeListener { _, month ->
            tv_month.text = month.toString() + "月"
        }
        btn_analyse_meal.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("startDate", start)
            bundle.putString("endDate", end)
        }
        fl_current.setOnClickListener {
            calendarView.clearSelectRange()
            tv_left_week.text = "开始日期"
            tv_right_week.text = "结束日期"
            tv_left_date.text = ""
            tv_right_date.text = ""
        }
    }


    override fun onCalendarSelectOutOfRange(calendar: Calendar?) {
        TODO("not implemented")
    }

    @SuppressLint("SetTextI18n")
    override fun onCalendarRangeSelect(calendar: Calendar, isEnd: Boolean) {

        val month = if (calendar.month < 10) {
            "0" + calendar.month
        } else {
            calendar.month
        }
        val day = if (calendar.day < 10) {
            "0" + calendar.day
        } else {
            calendar.day
        }
        if (!isEnd) {

            start = calendar.year.toString() + "-" + month + "-" + day
            tv_left_date.text = calendar.month.toString() + "月" + calendar.day + "日"
            tv_left_week.text = WEEK[calendar.week]
            tv_right_week.text = "结束日期"
            tv_right_date.text = ""
        } else {
            end = calendar.year.toString() + "-" + month + "-" + day
            tv_right_date.text = calendar.month.toString() + "月" + calendar.day + "日"
            tv_right_week.text = WEEK[calendar.week]
        }
    }

    companion object {
        private val WEEK = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
    }

    override fun onSelectOutOfRange(calendar: Calendar?, isOutOfMinRange: Boolean) {
        Toast.makeText(
            context,
            calendar.toString() + if (isOutOfMinRange) "小于最小选择范围" else "超过最大选择范围",
            Toast.LENGTH_SHORT
        ).show()
    }
}