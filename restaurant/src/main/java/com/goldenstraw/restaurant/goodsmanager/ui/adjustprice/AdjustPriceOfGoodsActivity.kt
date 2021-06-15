package com.goldenstraw.restaurant.goodsmanager.ui.adjustprice

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityAdjustPirceOfGoodsBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.queryordersactivitymodule
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewPrice
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.Goods
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 调整价格，当供应商提交新单价时，管理员对新价格进行确认或取消。
 * 当确认时需要修改两个地方的单价：一是商品信息，二是尚未录入即状态为4的订单单价
 */
class AdjustPriceOfGoodsActivity : BaseActivity<ActivityAdjustPirceOfGoodsBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_adjust_pirce_of_goods
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(queryordersactivitymodule)
    }

    var adapter = BaseDataBindingAdapter(
        layoutId = R.layout.layout_goods_item,
        dataBinding = { LayoutGoodsItemBinding.bind(it) },
        dataSource = { viewModel!!.goodsList },
        callback = { goods, binding, _ ->
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
    private val repository by instance<QueryOrdersRepository>()
    var viewModel: QueryOrdersViewModel? = null

    override fun initView() {
        super.initView()
        viewModel = getViewModel {
            QueryOrdersViewModel(repository)
        }
        viewModel!!.defUI.refreshEvent.observe(this) {
            adapter.forceUpdate()
        }
        viewModel!!.defUI.showDialog.observe(this) {
            AlertDialog.Builder(this)
                .setMessage(it)
                .create()
                .show()
        }
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
            .setNegativeButton("不同意") { dialog, _ ->
                //申请清零,原价格不变
                val newPrice = NewPrice(0.0f, goods.unitPrice)
                goods.newPrice = 0.0f
                viewModel!!.updateNewPriceOfGoods(newPrice, goods.objectId)
                dialog.dismiss()
            }
            .setPositiveButton("同意") { dialog, _ ->
                if (edit.text.isNullOrEmpty()) {
                    return@setPositiveButton
                }
                val newPrice = edit.text.toString().trim().toFloat()
                //新价格清零，原价格改为新价格
                val newGoods = NewPrice(0.0f, newPrice)
                goods.unitPrice = newPrice
                //修改商品信息中的单价
                viewModel!!.updateNewPriceOfGoods(newGoods, goods.objectId)
                //确认修改价格时，有的商品已经形成订单，所以此时要把还没有记帐的订单中商品价格改为新价格
                updatePriceOfOrders(goods.goodsName, newPrice)
                adapter.forceUpdate()
                dialog.dismiss()
            }.create()
        dialog.show()

    }

    /**
     * 修改订单中的单价。首先依据商品名称和状态等于1为条件，找到订单，然后对这个订单进行修改
     * 主要是因为下单后才发现价格发生变化。因为一旦验货后，商品小计即可生成，所以再更改单价
     * 将出现数量与单价积不等于小计值的现象。
     */
    private fun updatePriceOfOrders(goodsName: String, newPrice: Float) {
        val where = "{\"\$and\":[{\"goodsName\":\"$goodsName\"},{\"state\":{\"\$in\":[0,1]}}]}"
        viewModel!!.updateUnitPriceOfOrders(where, newPrice)
    }

    /**
     * 获取所有需要调整价格的商品信息,新价格不等于0的，即为供应商申请调整价格的商品
     */
    private fun getAllGoodsOfAdjustPrice() {
        val where = "{\"newPrice\":{\"\$gt\":0}}"
        viewModel!!.getAllGoodsOfCategory(where)

    }

}