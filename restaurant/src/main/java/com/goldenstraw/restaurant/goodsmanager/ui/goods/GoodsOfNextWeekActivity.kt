package com.goldenstraw.restaurant.goodsmanager.ui.goods

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ObservableField
import androidx.lifecycle.observe
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityNextWeekGoodsBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.databinding.ViewpageOfCookKindBinding
import com.goldenstraw.restaurant.goodsmanager.di.queryordersactivitymodule
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewPrice
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.Goods
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_next_week_goods.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 获取下个月需要的商品信息。分类显示
 */
class GoodsOfNextWeekActivity : BaseActivity<ActivityNextWeekGoodsBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_next_week_goods
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(queryordersactivitymodule)
    }

    private val repository by instance<QueryOrdersRepository>()

    var viewModel: QueryOrdersViewModel? = null

    //    var adapter: BaseDataBindingAdapter<Goods, LayoutGoodsItemBinding>? = null
    var vpAdapter: BaseDataBindingAdapter<String, ViewpageOfCookKindBinding>? = null

    var tabLayoutMediator: TabLayoutMediator? = null

    var state = ObservableField<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel {
            QueryOrdersViewModel(repository)
        }
        vpAdapter = BaseDataBindingAdapter(
            layoutId = R.layout.viewpage_of_cook_kind,
            dataSource = { viewModel!!.groupbyCategoryOfGoods.keys.toList() },
            dataBinding = { ViewpageOfCookKindBinding.bind(it) },
            callback = { category, binding, _ ->
                var adapter = BaseDataBindingAdapter(
                    layoutId = R.layout.layout_goods_item,
                    dataBinding = { LayoutGoodsItemBinding.bind(it) },
                    dataSource = {
                        viewModel!!.groupbyCategoryOfGoods[category]!!
                    },
                    callback = { goods, binding, position ->
                        binding.goods = goods
                        binding.addSub.visibility = View.INVISIBLE
                        binding.cbGoods.visibility = View.INVISIBLE
                        binding.clickEvent = object : Consumer<Goods> {
                            override fun accept(t: Goods) {
                                popUpNewPriceDialog(goods)
                            }

                        }
                    }
                )
                binding.rlvCookbook.adapter = adapter
            }
        )


        /*
         观察各种事件，刷新，错误提示，加载等
         */
        viewModel!!.defUI.refreshEvent.observe(this) {
            if (viewModel!!.groupbyCategoryOfGoods == null) {
                state.set(MultiStateView.VIEW_STATE_EMPTY)
            } else {
                state.set(MultiStateView.VIEW_STATE_CONTENT)
                category_goods_toolbar.title = "下周拟购商品"
                vpAdapter!!.forceUpdate()
            }
        }
        viewModel!!.defUI.toastEvent.observe(this) {
            state.set(MultiStateView.VIEW_STATE_ERROR)
            AlertDialog.Builder(this)
                .setMessage(it)
                .create().show()
        }

        viewModel!!.defUI.loadingEvent.observe(this) {
            state.set(MultiStateView.VIEW_STATE_LOADING)
        }
        vp_goods.adapter = vpAdapter
        tabLayoutMediator = TabLayoutMediator(tab_goods_category, vp_goods) { tab, position ->

            tab.text = "" + (position + 1)

        }
        tabLayoutMediator?.attach()
        /*
         *默认是获取下周拟购商品信息
         * 可以通过菜
         */
        viewModel!!.getAllCookBookOfDailyMeal()
    }

    /**
     * 弹出输入新单价的窗口
     */
    @SuppressLint("AutoDispose")
    fun popUpNewPriceDialog(goods: Goods) {
        val view =
            LayoutInflater.from(this).inflate(R.layout.only_input_number_dialog_view, null)
        val edit = view.findViewById<EditText>(R.id.number_edit)
        val dialog = AlertDialog.Builder(this)
            .setTitle("申请调整\"${goods.goodsName}\"的单价")
            .setIcon(R.mipmap.add_icon)
            .setView(view)
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                if (edit.text.isNullOrEmpty()) {
                    return@setPositiveButton
                }
                val newPrice = edit.text.toString().trim().toFloat()
                goods.newPrice = newPrice
                val newGoods = NewPrice(newPrice, goods.unitPrice)
                viewModel!!.updateNewPriceOfGoods(newGoods, goods.objectId)
                    .subscribeOn(Schedulers.io())
                    .subscribe()
                vpAdapter!!.forceUpdate()
                dialog.dismiss()
            }.create()
        dialog.show()

    }
}