package com.goldenstraw.restaurant.goodsmanager.ui.supplier

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSupplierCategoryGoodsBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewPrice
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.Goods
import kotlinx.android.synthetic.main.fragment_supplier_category_goods.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 用于显示某类商品的定价，便于供应商及时了解到所提供商品的价格信息。
 * 供应商每周初可以申请一次调价，如因市场变化需要临时调价，主动书面提请，由管理员认可后，修改。
 * 供应商可以对商品价格进行申请修改.
 * [这里存在一个问题，如果多家供应商同时对同一商品进行价格调整时，怎么办？原则是取较小值，怎么实现这一功能]
 * 注：管理员确认申请后会将申请调价清零。
 */
class CategoryGoodsInfoFragment : BaseFragment<FragmentSupplierCategoryGoodsBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_supplier_category_goods
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)

    }

    private val prefs by instance<PrefsHelper>()
    private val repository by instance<QueryOrdersRepository>()
    var viewModel: QueryOrdersViewModel? = null
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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(category_goods_toolbar)
        setHasOptionsMenu(true)


        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }

        /*
         观察各种事件，刷新，错误提示，加载等
         */
        viewModel!!.defUI.refreshEvent.observe(viewLifecycleOwner) {
            adapter.forceUpdate()
        }

        viewModel!!.getGoodsOfCategory(prefs.categoryCode)

    }

    /**
     * 弹出输入新单价的窗口
     */
    @SuppressLint("AutoDispose")
    fun popUpNewPriceDialog(goods: Goods) {
        val view =
            LayoutInflater.from(context).inflate(R.layout.only_input_number_dialog_view, null)
        val edit = view.findViewById<EditText>(R.id.number_edit)
        val dialog = AlertDialog.Builder(context!!)
            .setTitle("申请调整\"${goods.goodsName}\"的单价")
            .setIcon(R.mipmap.add_icon)
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                if (edit.text.isNullOrEmpty()) {
                    return@setPositiveButton
                }
                val newPrice = edit.text.toString().trim().toFloat()
                goods.newPrice = newPrice
                val newGoods = NewPrice(newPrice, goods.unitPrice)
                viewModel!!.updateNewPriceOfGoods(newGoods, goods.objectId)
                dialog.dismiss()
            }.create()
        dialog.show()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_get_goods_info, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.all_goods -> {
                viewModel!!.getGoodsOfCategory(prefs.categoryCode)
            }
            R.id.next_week_goods -> {

            }
        }
        return true
    }
}