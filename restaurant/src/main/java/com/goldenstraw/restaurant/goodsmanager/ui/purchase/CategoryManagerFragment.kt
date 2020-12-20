package com.goldenstraw.restaurant.goodsmanager.ui.purchase

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.observe
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCategoryListBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsCategoryBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.repositories.goods_order.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.GoodsToOrderMgViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.util.toast
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_category_list.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class CategoryManagerFragment : BaseFragment<FragmentCategoryListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_category_list
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(goodsDataSourceModule)
    }
    private val repository by instance<GoodsRepository>()

    /*
      使用同一个Activity范围下的共享ViewModel
     */
    var viewModelGoodsTo: GoodsToOrderMgViewModel? = null
    var adapter: BaseDataBindingAdapter<GoodsCategory, LayoutGoodsCategoryBinding>? = null

    var categoryFlow = mutableListOf<GoodsCategory>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelGoodsTo = activity?.getViewModel {
            GoodsToOrderMgViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_goods_category,
            dataBinding = { LayoutGoodsCategoryBinding.bind(it) },
            dataSource = { categoryFlow },
            callback = { goodsCategory, binding, _ ->
                binding.apply {
                    category = goodsCategory
                    goodsEvent = object : Consumer<GoodsCategory> {
                        override fun accept(t: GoodsCategory) {
                            viewModelGoodsTo!!.selected.value = t

                            categoryFlow.forEach {
                                it.isSelected = false
                            }
                            t.isSelected = true
                            adapter!!.forceUpdate()
                        }
                    }
                    tvCategoryName.isSelected = goodsCategory.isSelected
                    if (goodsCategory.isSelected) {
                        view.visibility = View.VISIBLE
                    } else {
                        view.visibility = View.INVISIBLE
                    }
                }
            }

        )

//        viewModelGoodsTo!!.getIsRefresh().observe(viewLifecycleOwner, Observer {
//            if (it)
//                adapter!!.forceUpdate()
//        })
        /*
         * 因为从Room中得到的Flow转换LiveData后，数据库中数据的变化都会被观察到，
         * 所以对数据的任何操作（增改删）都不需要再额外添加刷新列表的操作
         */
        viewModelGoodsTo!!.categoryListFlow.observe(viewLifecycleOwner) {
            categoryFlow = it as MutableList<GoodsCategory>
            categoryFlow.first().let { it ->
                it.isSelected = true
                viewModelGoodsTo!!.selected.value = it
            }
            adapter!!.forceUpdate()
        }
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
                .setWidth(120)
            leftMenu.addMenuItem(deleteItem)
            val updateItem = SwipeMenuItem(context)
                .setBackground(R.color.secondaryColor)
                .setText("修改")
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setWidth(120)
            rightMenu.addMenuItem(updateItem)
        }
        /*
         2、关联RecyclerView，设置侧滑菜单
         */
        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator)
        /*
        3、定义子菜单点击事件
         */
        val mItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            when (menuBridge.direction) {//判断 1是左侧，-1是右侧的菜单
                -1 -> {
                    when (menuBridge.position) {
                        0 -> {
                            val category = categoryFlow[adapterPosition]
                            updateDialog(category)
                        }
                    }
                }
                1 -> {
                    when (menuBridge.position) {
                        0 -> if (viewModelGoodsTo!!.goodsList.isEmpty()) {
                            deleteDialog(adapterPosition)
                        } else {
                            Toast.makeText(
                                context, "类别中有商品不能删除！！",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            }

        }
        /*
        4、给RecyclerView添加监听器
         */
        recyclerView.setOnItemMenuClickListener(mItemMenuClickListener)
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

                viewModelGoodsTo!!.deleteCategory(categoryFlow[position])
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                    }, {
                        Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                    })
//                categoryFlow.removeAt(position)
//                adapter!!.forceUpdate()
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    /**
     * 修改对话框
     */
    @SuppressLint("AutoDispose")
    private fun updateDialog(category: GoodsCategory) {
        val view = layoutInflater.inflate(R.layout.add_or_edit_one_dialog_view, null)
        val name = view.findViewById<EditText>(R.id.dialog_edit)
        name.setText(category.categoryName)
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_update_name)
            .setTitle("修改类别信息")
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                val name = name.text.toString().trim()
                if (name.isNullOrEmpty()) {
                    toast { "请填写必须内容！！" }
                } else {
                    category.categoryName = name
                    viewModelGoodsTo!!.updateCategory(category)
                        .subscribeOn(Schedulers.computation())
                        .subscribe()
//                    adapter!!.forceUpdate()
                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }
}