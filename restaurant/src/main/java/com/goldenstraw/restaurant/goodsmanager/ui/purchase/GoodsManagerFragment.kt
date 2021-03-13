package com.goldenstraw.restaurant.goodsmanager.ui.purchase

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentGoodsListBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.goldenstraw.restaurant.goodsmanager.repositories.goods_order.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.GoodsToOrderMgViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.util.toast
import com.youth.banner.BannerConfig
import com.youth.banner.loader.ImageLoader
import kotlinx.android.synthetic.main.fragment_goods_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class GoodsManagerFragment : BaseFragment<FragmentGoodsListBinding>() {

    private val prefs by instance<PrefsHelper>()
    override val layoutId: Int
        get() = R.layout.fragment_goods_list

    override val kodein: Kodein = Kodein.lazy {

        extend(parentKodein, copy = Copy.All)

        import(goodsDataSourceModule)
    }

    //通过Kodein容器检索对象
    private val repository by instance<GoodsRepository>()

    //使用同一个Activity范围下的共享ViewModel
    var viewModelGoodsTo: GoodsToOrderMgViewModel? = null


    val adapter = BaseDataBindingAdapter(
        layoutId = R.layout.layout_goods_item,
        dataSource = {
            viewModelGoodsTo!!.goodsList
        },
        dataBinding = { LayoutGoodsItemBinding.bind(it) },
        callback = { goods, binding, position ->
            binding.goods = goods
            binding.checkEvent = object : Consumer<Goods> {
                override fun accept(t: Goods) {
                    t.isChecked = !t.isChecked
                    binding.cbGoods.isChecked = t.isChecked
                }
            }
            binding.clickEvent=object :Consumer<Goods>{
                override fun accept(t: Goods) {
                    t.isChecked = !t.isChecked
                    binding.cbGoods.isChecked = t.isChecked
                }
            }
            binding.longClick = object : Consumer<Goods> {
                override fun accept(t: Goods) {
                    managerDialog(t)
                }
            }
            binding.cbGoods.isChecked = goods.isChecked
            binding.addSub
                .setBuyMin(0)
                .setCurrentNumber(0)
                .setPosition(position)
                .setOnChangeValueListener { value, _ ->
                    goods.quantity = value
                }
        }
    )

    //轮播图片
    private val images =
        mutableListOf(R.mipmap.blueshipin, R.mipmap.zhurou, R.mipmap.shucai, R.mipmap.tiaoliao)

    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelGoodsTo = activity?.getViewModel {
            GoodsToOrderMgViewModel(repository)
        }
        viewModelGoodsTo!!.goodsListFlow.observe(viewLifecycleOwner) {
            viewModelGoodsTo!!.apply {
                goodsList = it as MutableList<Goods>
                if (goodsList.isEmpty()) {
                    goodsLoadState.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    goodsLoadState.set(MultiStateView.VIEW_STATE_CONTENT)
                }
                setRefresh(true)
            }
        }
        viewModelGoodsTo!!.isRefresh.observe(viewLifecycleOwner) {
            adapter.forceUpdate()
        }
        lifecycleScope.launch {
            with(mBinding.banner) {
                setImageLoader(object : ImageLoader() {
                    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
                        Glide.with(context).load(path).into(imageView)
                    }
                })
                setImages(images)
                setDelayTime(5000)
                setIndicatorGravity(BannerConfig.RIGHT)
                start()
            }
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
                    goods.isChecked = false
                    viewModelGoodsTo!!.updateGoods(goods)
                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }

    /*
     * 增加数据
     */
    fun addDialog() {
        val view = layoutInflater.inflate(R.layout.add_or_edit_more_dialog_view, null)
        val goodsName = view.findViewById<EditText>(R.id.et_goods_name)
        val unitOfMeasure = view.findViewById<EditText>(R.id.et_unit_of_measure)
        val unitPrice = view.findViewById<EditText>(R.id.et_unit_price)
        unitPrice.visibility = View.VISIBLE
        val currentCategory = viewModelGoodsTo!!.currentCategory.value
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.mipmap.add_icon)
            .setTitle("增加商品")
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                val name = goodsName.text.toString().trim()
                val unit = unitOfMeasure.text.toString().trim()
                val price = unitPrice.text.toString().trim().toFloat()
                if (name.isEmpty()) {
                    toast { "请填写商品名称！！" }
                    return@setPositiveButton
                }
                if (unit.isEmpty()) {
                    toast { "请填写计量单位！！" }
                    return@setPositiveButton
                }
                if (price == 0.0f) {
                    toast { "请填写商品单价！！" }
                    return@setPositiveButton
                }
                val goods = NewGoods(
                    goodsName = name,
                    unitOfMeasurement = unit,
                    categoryCode = currentCategory!!,
                    unitPrice = price
                )
                viewModelGoodsTo!!.addGoods(goods)
                dialog.dismiss()

            }.create()
        dialog.show()
    }

    /**************************************************
     * 加入购物车:将选择的商品加入购物车。为了是避免重复选择，从
     * 当前显示的列表中删除已加入的商品，并非真正删除。
     *************************************************/
    fun addGoodsToShoppingCart() {
        viewModelGoodsTo!!.apply {
            //还原商品信息
            val selectedList = mutableListOf<Goods>()
            goodsList.forEach {
                if (it.isChecked) {
                    it.isChecked = false
                    selectedList.add(it)
                }
            }
            addGoodsToShoppingCar(selectedList, prefs.district)
            goodsList.removeAll(selectedList)
        }
        adapter.forceUpdate()
    }
}