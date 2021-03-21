package com.goldenstraw.restaurant.goodsmanager.ui.purchase

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentGoodsListBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.repositories.goods_order.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.GoodsToOrderMgViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.util.toast
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class GoodsSearchFragment : BaseFragment<FragmentGoodsListBinding>() {

    private val prefs by instance<PrefsHelper>()

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(goodsDataSourceModule)
    }
    override val layoutId: Int
        get() = R.layout.fragment_search_goods
    private val repository by instance<GoodsRepository>()
    var viewModelGoodsTo: GoodsToOrderMgViewModel? = null
    var adapter = BaseDataBindingAdapter(
        layoutId = R.layout.layout_goods_item,
        dataSource = { viewModelGoodsTo!!.searchGoodsResultList },
        dataBinding = { LayoutGoodsItemBinding.bind(it) },
        callback = { goods, binding, position ->
            binding.goods = goods
            binding.checkEvent = object : Consumer<Goods> {
                override fun accept(t: Goods) {
                    t.isChecked = !t.isChecked
                    binding.cbGoods.isChecked = t.isChecked
                }
            }
            binding.cbGoods.isChecked = goods.isChecked //这里设置的是初始状态

            binding.longClick = object : Consumer<Goods> {
                override fun accept(t: Goods) {
                    managerDialog(t)
                }
            }
            binding.addSub
                .setBuyMin(0)
                .setCurrentNumber(0)
                .setPosition(position)
                .setOnChangeValueListener { value, _ ->
                    goods.quantity = value
                }
        }
    )


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelGoodsTo = activity?.getViewModel {
            GoodsToOrderMgViewModel(repository)
        }
        viewModelGoodsTo!!.searchResult.observe(viewLifecycleOwner) {
            viewModelGoodsTo!!.searchGoodsResultList = it
            adapter.forceUpdate()
        }
        viewModelGoodsTo!!.isRefresh.observe(viewLifecycleOwner) {
            adapter.forceUpdate()
        }
        viewModelGoodsTo!!.defUI.showDialog.observe(viewLifecycleOwner) {
            androidx.appcompat.app.AlertDialog.Builder(context!!)
                .setMessage(it)
                .create()
                .show()
        }
    }

    /********************************************************
     * 管理数据对话框
     *******************************************************/
    /*
    管理数据
     */
    private fun managerDialog(goods: Goods) {
        val view = layoutInflater.inflate(R.layout.delete_or_update_dialog_view, null)
        val delete = view.findViewById<Button>(R.id.delete_action)
        val update = view.findViewById<Button>(R.id.update_action)
        val managerDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        managerDialog.show()
        delete.setOnClickListener {
            deleteDialog(goods)
            managerDialog.dismiss()
        }
        update.setOnClickListener {
            updateDialog(goods)
            managerDialog.dismiss()
        }
    }

    /*
     * 删除数据
     */
    @SuppressLint("AutoDispose")
    private fun deleteDialog(goods: Goods) {
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_alert_name)
            .setTitle("确定删除")
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                viewModelGoodsTo!!.deleteGoods(goods)

                viewModelGoodsTo!!.searchGoodsResultList.remove(goods)

                adapter.forceUpdate()
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    /*
     * 修改数据
     */
    @SuppressLint("AutoDispose")
    private fun updateDialog(goods: Goods) {
        val view = layoutInflater.inflate(R.layout.add_or_edit_more_dialog_view, null)
        val goodsName = view.findViewById<EditText>(R.id.et_goods_name)
        goodsName.setText(goods.goodsName)
        val unitOfMeasure = view.findViewById<EditText>(R.id.et_unit_of_measure)
        unitOfMeasure.setText(goods.unitOfMeasurement)
        val unitPrice = view.findViewById<EditText>(R.id.et_unit_price)
        unitPrice.setText(goods.unitPrice.toString())
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_update_name)
            .setTitle("修改商品信息")
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                val name = goodsName.text.toString().trim()
                val unit = unitOfMeasure.text.toString().trim()
                val price = unitPrice.text.toString().trim().toFloat()
                if (name.isEmpty() || unit.isEmpty()) {
                    toast { "请填写必须内容！！" }
                } else {
                    goods.goodsName = name
                    goods.unitOfMeasurement = unit
                    goods.unitPrice = price
                    viewModelGoodsTo!!.updateGoods(goods)
                    adapter.forceUpdate()
                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }

    /**
     * 加入购物车
     */
    fun addGoodsToShoppingCar() {
        viewModelGoodsTo!!.apply {
            //还原商品信息
            val selectedList = mutableListOf<Goods>()
            searchGoodsResultList.forEach {
                if (it.isChecked) {
                    it.isChecked = false
                    selectedList.add(it)
                }
            }
            addGoodsToShoppingCar(selectedList, prefs.district)
            searchGoodsResultList.removeAll(selectedList)
        }

        adapter.forceUpdate()
    }
}