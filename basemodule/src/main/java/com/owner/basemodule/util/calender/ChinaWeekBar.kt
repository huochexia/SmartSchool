package com.owner.basemodule.util.calender

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.TextView
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.WeekBar
import com.owner.basemodule.R

/**
 * 中国日历周标栏
 */
class ChinaWeekBar(context: Context) : WeekBar(context) {

    private var mPreSelectedIndex: Int = 0

    init {
        //加载布局
        LayoutInflater.from(context).inflate(R.layout.china_week_bar, this, true)
        setBackgroundColor(Color.WHITE)
        val padding = dipToPx(context, 10f)
        setPadding(padding, 0, padding, 0)
    }

    /**
     * 日期选择事件，这里提供这个回调，可以方便定制WeekBar需要
     *
     * @param calendar  calendar 选择的日期
     * @param weekStart 周起始
     * @param isClick   isClick 点击
     */
    override fun onDateSelected(calendar: Calendar?, weekStart: Int, isClick: Boolean) {
//        getChildAt(mPreSelectedIndex).isSelected = false
//        val viewIndex = getViewIndexByCalendar(calendar!!, weekStart)
//        getChildAt(viewIndex).isSelected = true
//        mPreSelectedIndex = viewIndex
    }

    /**
     * 当周起始发生变化，使用自定义布局需要重写这个方法，避免出问题
     *
     * @param weekStart 周起始
     */
    override fun onWeekStartChange(weekStart: Int) {
        for (i in 0 until childCount) {
            (getChildAt(i) as TextView).text = getWeekString(i, weekStart)
        }
    }

    /**
     * 获取周文本，这个方法仅供父类使用，这是显示内容
     *
     * @param index     index
     * @param weekStart weekStart
     * @return 获取周文本
     */
    private fun getWeekString(index: Int, weekStart: Int): String {
        val weeks = context.resources.getStringArray(R.array.chinese_week_string_array)

        if (weekStart == 1) {
            return weeks[index]
        }
        return if (weekStart == 2) {
            weeks[if (index == 6) 0 else index + 1]
        } else weeks[if (index == 0) 6 else index - 1]
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private fun dipToPx(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}
