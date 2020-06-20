package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentDailyMealSelectDateBinding
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CookBookViewModel
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import kotlinx.android.synthetic.main.fragment_single_date_select.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class DailyMealDateFragment : BaseFragment<FragmentDailyMealSelectDateBinding>(),
    CalendarView.OnCalendarSelectListener {


    override val layoutId: Int
        get() = R.layout.fragment_daily_meal_select_date

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

        override fun initView() {
        //点击小日历返回当前日期
        fl_current.setOnClickListener { calendarView.scrollToCurrent() }

        calendarView.setOnCalendarSelectListener(this)

        tv_year.text = calendarView.curYear.toString()
        tv_month_day.text = calendarView.curMonth.toString() + "月" + calendarView.curDay + "日"
        tv_current_day.text = calendarView.curDay.toString()
        tv_lunar.text = "今日"
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
        tv_year.text = calendar.year.toString()
        tv_lunar.text = calendar.lunar
        if (isClick) {
            val bundle = Bundle()
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
            val date = calendar.year.toString() + "-" + month + "-" + day
            bundle.putString("mealdate", date)

            findNavController().navigate(R.id.dailyMealTimeFragment, bundle)

        }
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}