package com.goldenstraw.restaurant.goodsmanager.ui.check

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCheckSelectDateBinding
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_single_date_select.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * Created by Administrator on 2019/10/29 0029
 */
class CheckSelectDateFragment : BaseFragment<FragmentCheckSelectDateBinding>(),
    CalendarView.OnCalendarSelectListener {


    override val layoutId: Int
        get() = R.layout.fragment_check_select_date

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
    }
    private val prefs by instance<PrefsHelper>()

    private val repository by instance<VerifyAndPlaceOrderRepository>()
    var viewModel: VerifyAndPlaceOrderViewModel? = null
    val map = HashMap<String, Calendar>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
        markDate()
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
            val date = calendar.year.toString() + "-" + calendar.month + "-" + calendar.day
            bundle.putString("orderDate", date)
            bundle.putInt("district",prefs.district)
            findNavController().navigate(R.id.haveOrdersOfSupplierList, bundle)
        }
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 标记尚未记帐的日期
     */
    private fun markDate() {
        val where = "{\"\$and\":[{\"state\":1},{\"quantity\":{\"\$ne\":0}},{\"district\":${prefs.district}}]}"
        viewModel!!.getAllOrderOfDate(where)
            .flatMap {
                Observable.fromIterable(it)
            }.map {
                it.orderDate
            }
            .distinct()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                val string = it.split("-")
                val year = string[0]
                val month = string[1]
                val day = string[2]
                map[getSchemeCalendar(
                    year.toInt(), month.toInt(), day.toInt(),
                    -0x20ecaa, "未验"
                ).toString()] = getSchemeCalendar(
                    year.toInt(), month.toInt(), day.toInt(),
                    -0x20ecaa, "未验"
                )

            }, {}, {
                calendarView.setSchemeDate(map)
            })
    }


}