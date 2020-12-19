package com.goldenstraw.restaurant.goodsmanager.ui.goods

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentGoodsListBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.repositories.goods_order.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.GoodsToOrderMgViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.util.toast
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search_goods.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class GoodsSearchFragment : BaseFragment<FragmentGoodsListBinding>() {
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(goodsDataSourceModule)
    }
    override val layoutId: Int
        get() = R.layout.fragment_search_goods
    private val repository by instance<GoodsRepository>()
    var viewModelGoodsTo: GoodsToOrderMgViewModel? = null
    var adapter: BaseDataBindingAdapter<Goods, LayoutGoodsItemBinding>? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelGoodsTo = activity?.getViewModel {
            GoodsToOrderMgViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
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
                binding.addSub
                    .setCurrentNumber(goods.quantity)
                    .setPosition(position)
                    .setOnChangeValueListener { value, position ->
                        goods.quantity = value
                    }
            }
        )
        viewModelGoodsTo!!.getIsRefresh().observe(viewLifecycleOwner, Observer {
            if (it) {
                adapter!!.forceUpdate()
            }
        })
        initSwipeMenu()
    }
    /**
     * 初始化Item侧滑菜单
     */
    private fun initSwipeMenu() {
        /*
        1、生成子菜单，这里将子菜单设置在右侧
         */
        val mSwipeMenuCreator = SwipeMenuCreator { leftMenu, rightMenu, position ->
            val deleteItem = SwipeMenuItem(context)
                .setBackground(R.color.colorAccent)
                .setText("删除")
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setWidth(150)
            rightMenu.addMenuItem(deleteItem)
            val updateItem = SwipeMenuItem(context)
                .setBackground(R.color.secondaryColor)
                .setText("修改")
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setWidth(150)
            rightMenu.addMenuItem(updateItem)
        }
        /*
         2、关联RecyclerView，设置侧滑菜单
         */
        rlw_search_goods.setSwipeMenuCreator(mSwipeMenuCreator)
        /*
        3、定义子菜单点击事件
         */
        val mItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (menuBridge.position) {
                0 -> {
                    deleteDialog(adapterPosition)
                }
                1 -> {

                    updateDialog(viewModelGoodsTo!!.searchGoodsResultList[adapterPosition])
                }
            }
        }
        /*
        4、给RecyclerView添加监听器
         */
        rlw_search_goods.setOnItemMenuClickListener(mItemMenuClickListener)
    }

    /**
     * 删除对话框
     */
    @SuppressLint("AutoDispose")
    private fun deleteDialog(position: Int) {
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_alert_name)
            .setTitle("确定删除")
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->

                viewModelGoodsTo!!.deleteGoods(viewModelGoodsTo!!.searchGoodsResultList[position])
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                    }, {
                        Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                    })
                viewModelGoodsTo!!.searchGoodsResultList.removeAt(position)
                adapter!!.forceUpdate()
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    /**
     * 修改对话框
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
                if (name.isNullOrEmpty() || unit.isNullOrEmpty()) {
                    toast { "请填写必须内容！！" }
                } else {
                    goods.goodsName = name
                    goods.unitOfMeasurement = unit
                    goods.unitPrice = price
                    viewModelGoodsTo!!.updateGoods(goods).subscribeOn(Schedulers.computation())
                        .subscribe()
                    adapter!!.forceUpdate()
                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }

    /**
     * 加入购物车
     */
    fun addGoodsToShoppingCar() {
        viewModelGoodsTo!!.addGoodsToShoppingCar(viewModelGoodsTo!!.searchGoodsResultList)
        //还原商品信息
        val selectedList = mutableListOf<Goods>()
        viewModelGoodsTo!!.searchGoodsResultList.forEach {
            if (it.isChecked) {
                it.isChecked = false
                it.quantity = 1
                selectedList.add(it)
            }
        }
        viewModelGoodsTo!!.searchGoodsResultList.removeAll(selectedList)
        adapter!!.forceUpdate()
    }
}