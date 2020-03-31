package com.goldenstraw.restaurant.goodsmanager.ui.adjustprice

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityAdjustPirceOfGoodsBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.queryordersactivitymodule
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewPrice
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.Goods
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 调整价格，当供应商提交新单价时，管理员对新价格进行确认、取消或重新指定价格。
 */
class AdjustPriceOfGoodsActivity : BaseActivity<ActivityAdjustPirceOfGoodsBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_adjust_pirce_of_goods
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(queryordersactivitymodule)
    }
    val state = ObservableField<Int>()
    var adapter: BaseDataBindingAdapter<Goods, LayoutGoodsItemBinding>? = null
    private val repository: QueryOrdersRepository by instance()
    var viewModel: QueryOrdersViewModel? = null
    var goodsList = mutableListOf<Goods>()

    override fun initView() {
        super.initView()
        viewModel = getViewModel {
            QueryOrdersViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_goods_item,
            dataBinding = { LayoutGoodsItemBinding.bind(it) },
            dataSource = { goodsList },
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
        getAllGoodsOfAdjustPrice()
    }

    /**
     * 弹出输入新单价的窗口
     */
    @SuppressLint("AutoDispose")
    fun popUpNewPriceDialog(goods: Goods) {
        val view =
            LayoutInflater.from(this).inflate(R.layout.only_input_number_dialog_view, null)
        val edit = view.findViewById<EditText>(R.id.number_edit)
        edit.setText(goods.newPrice.toString())
        val dialog = AlertDialog.Builder(this)
            .setTitle("申请调整\"${goods.goodsName}\"的单价")
            .setIcon(R.mipmap.add_icon)
            .setView(view)
            .setNegativeButton("不同意") { dialog, which ->
                //申请清零,原价格不变
                val newPrice = NewPrice(0.0f, goods.unitPrice)
                goods.newPrice = 0.0f
                viewModel!!.updateNewPriceOfGoods(newPrice, goods.objectId)
                    .subscribeOn(Schedulers.io())
                    .subscribe()
                dialog.dismiss()
            }
            .setPositiveButton("同意") { dialog, which ->
                if (edit.text.isNullOrEmpty()) {
                    return@setPositiveButton
                }
                val newPrice = edit.text.toString().trim().toFloat()
                //新价格清零，原价格改为新价格
                val newGoods = NewPrice(0.0f, newPrice)
                goods.unitPrice = newPrice

                viewModel!!.updateNewPriceOfGoods(newGoods, goods.objectId)
                    .subscribeOn(Schedulers.io())
                    .subscribe()
                adapter!!.forceUpdate()
                dialog.dismiss()
            }.create()
        dialog.show()

    }

    /**
     * 获取所有需要调整价格的商品信息,新价格不等于0的，即为供应商申请调整价格的商品
     */
    fun getAllGoodsOfAdjustPrice() {
        //where={"score":{"$ne":[1,3,5,7,9]}}' \
        val where = "{\"newPrice\":{\"\$gt\":0}}"
        viewModel!!.getGoodsOfCategory(where)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                if (it.isEmpty()) {
                    state.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    state.set(MultiStateView.VIEW_STATE_CONTENT)
                }
                goodsList.clear()
                goodsList.addAll(it)
                adapter!!.forceUpdate()
            }, {
                state.set(MultiStateView.VIEW_STATE_ERROR)
            }, {}, {
                state.set(MultiStateView.VIEW_STATE_LOADING)
            })
    }

}