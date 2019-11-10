package com.goldenstraw.restaurant.goodsmanager.ui.supplier

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSingleDateSelectBinding
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_single_date_select.calendarView
import kotlinx.android.synthetic.main.fragment_single_date_select.fl_current
import kotlinx.android.synthetic.main.fragment_single_date_select.tv_current_day
import kotlinx.android.synthetic.main.fragment_single_date_select.tv_lunar
import kotlinx.android.synthetic.main.fragment_single_date_select.tv_month_day
import kotlinx.android.synthetic.main.fragment_single_date_select.tv_year
import kotlinx.android.synthetic.main.fragment_supplier_date_select.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class SupplierQueryOrderFragment : BaseFragment<FragmentSingleDateSelectBinding>()
    , CalendarView.OnCalendarSelectListener {

    override val layoutId: Int
        get() = R.layout.fragment_supplier_date_select

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    private val repository: QueryOrdersRepository by instance()
    var viewModel: QueryOrdersViewModel? = null
    val map = HashMap<String, Calendar>()
    val prefs: PrefsHelper by instance()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }
        range_select_date.setOnClickListener {
            findNavController().navigate(R.id.supplierAccount)
        }

        markDate()
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        //点击小日历返回当前日期
        fl_current.setOnClickListener {
            calendarView.scrollToCurrent()
            findNavController().navigate(R.id.categoryGoodsInfoFragment)
        }
        calendarView.setOnCalendarSelectListener(this)
        tv_year.text = calendarView.curYear.toString()
        tv_month_day.text = calendarView.curMonth.toString() + "月" + calendarView.curDay + "日"
        tv_current_day.text = calendarView.curDay.toString()
        tv_lunar.text = "今日"

    }

    /**
     * 留着学习用
     */

//    private fun initData() {
//        val year = calendarView.curYear
//        val month = calendarView.curMonth
//        val map = HashMap<String, Calendar>()
//        map[getSchemeCalendar(year, month, 3, -0x40db25, "进货").toString()] =
//            getSchemeCalendar(year, month, 3, -0x40db25, "进货")
//        map[getSchemeCalendar(year, month, 6, -0x196ec8, "验货").toString()] =
//            getSchemeCalendar(year, month, 6, -0x196ec8, "验货")
//        map[getSchemeCalendar(year, month, 9, -0x20ecaa, "确认").toString()] =
//            getSchemeCalendar(year, month, 9, -0x20ecaa, "确认")
//        map[getSchemeCalendar(year, month, 13, -0x123a93, "录入").toString()] =
//            getSchemeCalendar(year, month, 13, -0x123a93, "录入")
//
//        calendarView.setSchemeDate(map)
//
//    }

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
        //因为每次初始化视图时都会执行这个方法，所以只有是点击事件时才进行跳转。如果不加上这个判断，
        //当回退到这个视图时就会调用跳转方法，这样形成一个死循环。
        if (isClick) {
            val bundle = Bundle()
            val date = calendar!!.year.toString() + "-" + calendar!!.month + "-" + calendar!!.day
            bundle.putString("date", date)
            val supplier = viewModel!!.supplier
            bundle.putString("supplier", supplier)
            findNavController().navigate(R.id.supplierOrderOfDate, bundle)
        }
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 标记有订单的日期，状态等于
     */
    private fun markDate() {
        val where = "{\"\$and\":[{\"state\":1},{\"supplier\":\"${prefs.username}\"}]}"
        viewModel!!.getOrdersOfSupplier(where)
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
                    -0x20ecaa, "有货"
                ).toString()] = getSchemeCalendar(
                    year.toInt(), month.toInt(), day.toInt(),
                    -0x20ecaa, "有货"
                )

            }, {}, {
                calendarView.setSchemeDate(map)
            })
    }
}