package com.goldenstraw.restaurant.goodsmanager.ui


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.goldenstraw.restaurant.R
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView

import kotlinx.android.synthetic.main.activity_range_date_select.*

class RangeDateActivity : AppCompatActivity(),
    CalendarView.OnCalendarRangeSelectListener, View.OnClickListener {

    private val layoutId: Int
        get() = R.layout.activity_range_date_select

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        initView()
        initEvent()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {

        calendarView.setOnCalendarRangeSelectListener(this)

        tv_commit.setOnClickListener(this)
        iv_clear.setOnClickListener(this)

        tv_month.text = calendarView.curMonth.toString() + "月"

    }

    private fun initEvent() {
        calendarView.setOnMonthChangeListener { _, month ->
            tv_month.text = month.toString() + "月"
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_clear -> {
                calendarView.clearSelectRange()
                tv_left_week.text = "开始日期"
                tv_right_week.text = "结束日期"
                tv_left_date.text = ""
                tv_right_date.text = ""
            }
            R.id.tv_commit -> {
                val calendars = calendarView.selectCalendarRange
                if (calendars == null || calendars.size == 0) {
                    return
                }
                for (c in calendars) {
                    Log.e(
                        "SelectCalendarRange", c.toString()
                                + " -- " + c.scheme
                                + "  --  " + c.lunar
                    )
                }
                Toast.makeText(
                    this, String.format(
                        "选择了%s个日期: %s —— %s", calendars.size,
                        calendars[0].toString(), calendars[calendars.size - 1].toString()
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }//mCalendarView.setSelectCalendarRange(2018,10,13,2018,10,13);
    }


    override fun onCalendarSelectOutOfRange(calendar: Calendar) {
        // TODO: 2018/9/13 超出范围提示
    }

    override fun onSelectOutOfRange(calendar: Calendar, isOutOfMinRange: Boolean) {
        Toast.makeText(
            this,
            calendar.toString() + if (isOutOfMinRange) "小于最小选择范围" else "超过最大选择范围",
            Toast.LENGTH_SHORT
        ).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onCalendarRangeSelect(calendar: Calendar, isEnd: Boolean) {
        if (!isEnd) {
            tv_left_date.text = calendar.month.toString() + "月" + calendar.day + "日"
            tv_left_week.text = WEEK[calendar.week]
            tv_right_week.text = "结束日期"
            tv_right_date.text = ""
        } else {
            tv_right_date.text = calendar.month.toString() + "月" + calendar.day + "日"
            tv_right_week.text = WEEK[calendar.week]
        }
    }

    companion object {
        private val WEEK = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
    }
}
