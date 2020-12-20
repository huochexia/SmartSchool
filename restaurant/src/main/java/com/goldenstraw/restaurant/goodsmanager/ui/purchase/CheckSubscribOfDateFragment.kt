package com.goldenstraw.restaurant.goodsmanager.ui.purchase

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCheckSubscribDateBinding
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.owner.basemodule.base.view.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_single_date_select.*
import org.kodein.di.Copy
import org.kodein.di.Kodein

/**
 *
 * 核查申购订单结果
 *
 */
class CheckSubscribOfDateFragment : BaseFragment<FragmentCheckSubscribDateBinding>(),
    CalendarView.OnCalendarSelectListener {

    override val layoutId: Int
        get() = R.layout.fragment_check_subscrib_date

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
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

    }

    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
        tv_month_day.visibility = View.VISIBLE
        tv_year.visibility = View.VISIBLE
        tv_month_day.text = calendar!!.month.toString() + "月" + calendar.day + "日"
        tv_year.text = calendar!!.year.toString()
        tv_lunar.text = calendar.lunar
        //因为每次初始化视图时都会执行这个方法，所以只有是点击事件时才进行跳转。如果不加上这个判断，
        //当回退到这个视图时就会调用跳转方法，这样形成一个死循环。
        if (isClick) {
            val bundle = Bundle()
            val date = calendar!!.year.toString() + "-" + calendar!!.month + "-" + calendar!!.day
            bundle.putString("orderDate", date)
            findNavController().navigate(R.id.allOrdersOfDateFragment, bundle)
        }
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {
        TODO("not implemented")
    }
}