package com.goldenstraw.restaurant.goodsmanager.ui.supplier


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSupplierAccountSelectBinding
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_supplier_account_select.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.text.DecimalFormat

class SupplierAccountFragment : BaseFragment<FragmentSupplierAccountSelectBinding>(),
    CalendarView.OnCalendarRangeSelectListener {

    override val layoutId: Int
        get() = R.layout.fragment_supplier_account_select
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    var supplier = ""
    lateinit var start: String
    lateinit var end: String
    private val repository: QueryOrdersRepository by instance()
    var viewModel: QueryOrdersViewModel? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }
        supplier = viewModel!!.supplier
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
        btn_query_order.setOnClickListener {
            findNavController().navigate(R.id.singleSelectCalender)
        }
        btn_account_orders.setOnClickListener {
            val calendars = calendarView.selectCalendarRange
            if (calendars == null || calendars.size == 0) {
                Toast.makeText(context, "请选择区间日期！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            accountOrders()
        }
        iv_clear.setOnClickListener {
            calendarView.clearSelectRange()
            tv_left_week.text = "开始日期"
            tv_right_week.text = "结束日期"
            tv_left_date.text = ""
            tv_right_date.text = ""

        }
        tv_account_detail.setOnClickListener {
            val calendars = calendarView.selectCalendarRange
            if (calendars == null || calendars.size == 0) {
                Toast.makeText(context, "请选择区间日期！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bundle = Bundle()
            bundle.putString("supplier", supplier)
            bundle.putString("start", start)
            bundle.putString("end", end)
            findNavController().navigate(R.id.detailedInventoryFragment, bundle)
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

    /**
     * 计算区间日期订单总额,已经记帐
     *
     */
    fun accountOrders() {
        tv_account_detail.visibility = View.VISIBLE
        val sum = 0.0f
        val where =
            "{\"\$and\":[{\"supplier\":\"$supplier\"}" +
                    ",{\"orderDate\":{\"\$gte\":\"$start\",\"\$lte\":\"$end\"}}" +
                    ",{\"state\":3}]}"
//        viewModel!!.getOrdersOfSupplier(where)
//            .flatMap {
//                Observable.fromIterable(it)
//            }
//            .subscribeOn(Schedulers.io())
//            .scan(sum) { sum, order ->
//                sum + order.total
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .autoDisposable(scopeProvider)
//            .subscribe({ sum ->
//                val format = DecimalFormat(".00")
//                tv_account_price.text = format.format(sum)
//
//            }, {}, {
//                tv_account_detail.visibility = View.VISIBLE
//            }, {
//                tv_account_price.text = "正在计算中....."
//            })
        viewModel!!.getTotalOfSupplier(where)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                val format = DecimalFormat(".00")
                if (it.isNotEmpty()) {
                    val sum = it[0]._sumTotal
                    tv_account_price.text = format.format(sum)
                }else{
                    tv_account_price.text = "没有值"
                }
            }, {

            }, {
                tv_account_detail.visibility = View.VISIBLE
            },{
                tv_account_price.text = "正在计算中......"
            })
    }
}
