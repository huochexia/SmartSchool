package com.goldenstraw.restaurant.goodsmanager.ui.purchase

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCategoryListBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsCategoryBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.repositories.goods_order.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.GoodsToOrderMgViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.util.toast
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    val adapter = BaseDataBindingAdapter(
        layoutId = R.layout.layout_goods_category,
        dataBinding = { LayoutGoodsCategoryBinding.bind(it) },
        dataSource = { viewModelGoodsTo!!.categoryList },
        callback = { goodsCategory, binding, _ ->
            binding.apply {
                category = goodsCategory
                //选择事件要完成两个任务：一是确定当前类别，二是刷新列表显示当前选中项目状态
                selectEvent = object : Consumer<GoodsCategory> {
                    override fun accept(t: GoodsCategory) {
                        viewModelGoodsTo!!.apply {
                            //1.确定当前类别
                            updateCurrentCategory(t.objectId)
                            //2.还原所有项目状态，设定当前选项状态，通知刷新列表
                            categoryList.forEach {
                                it.isSelected = false
                            }
                            t.isSelected = true
                            setRefresh(true)
                        }
                    }
                }
                longClick = object : Consumer<GoodsCategory> {
                    override fun accept(t: GoodsCategory) {
                        managerDialog(t)
                    }
                }
                tvCategoryName.isSelected = goodsCategory.isSelected
                if (goodsCategory.isSelected) {
                    signView.visibility = View.VISIBLE
                } else {
                    signView.visibility = View.INVISIBLE
                }
            }
        }

    )


    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelGoodsTo = activity?.getViewModel {
            GoodsToOrderMgViewModel(repository)
        }


        /*
         * 因为从Room中得到的Flow转换LiveData后，数据库中数据的变化都会被观察到，
         * 所以对数据的任何操作（增改删）都不需要再额外添加刷新列表的操作
         */
        viewModelGoodsTo!!.categoryListFlow.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                viewModelGoodsTo!!.categoryLoadState.set(MultiStateView.VIEW_STATE_EMPTY)
            } else {
                viewModelGoodsTo!!.apply {
                    categoryLoadState.set(MultiStateView.VIEW_STATE_CONTENT)
                    categoryList = it as MutableList<GoodsCategory>
                    categoryList.first().let { category ->
                        category.isSelected = true
                        updateCurrentCategory(category.objectId)
                    }
                    adapter.forceUpdate()
                }
            }
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


    /*************************************************************
     * 管理数据对话框
     ************************************************************/
    /*
      管理数据
     */
    private fun managerDialog(category: GoodsCategory) {
        val view = layoutInflater.inflate(R.layout.delete_or_update_dialog_view, null)
        val delete = view.findViewById<Button>(R.id.delete_action)
        val update = view.findViewById<Button>(R.id.update_action)
        val managerDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        managerDialog.show()

        delete.setOnClickListener {
            deleteDialog(category)
            managerDialog.dismiss()
        }

        update.setOnClickListener {
            updateDialog(category)
            managerDialog.dismiss()
        }

    }

    /*
     * 删除数据
     */
    @SuppressLint("AutoDispose")
    private fun deleteDialog(category: GoodsCategory) {
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_alert_name)
            .setTitle("确定删除")
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                viewModelGoodsTo!!.deleteCategory(category)
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    /*
     * 修改数据
     */
    @SuppressLint("AutoDispose")
    private fun updateDialog(category: GoodsCategory) {
        val view = layoutInflater.inflate(R.layout.add_or_edit_one_dialog_view, null)
        val input = view.findViewById<EditText>(R.id.dialog_edit)
        input.setText(category.categoryName)
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_update_name)
            .setTitle("修改类别信息")
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                val name = input.text.toString().trim()
                if (name.isEmpty()) {
                    toast { "请填写必须内容！！" }
                } else {
                    category.categoryName = name
                    category.isSelected = false
                    viewModelGoodsTo!!.updateCategory(category)

                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }
    /*
     * 增加数据
     */
    fun addDialog() {
        val view = layoutInflater.inflate(R.layout.add_or_edit_one_dialog_view, null)
        val editText = view.findViewById<EditText>(R.id.dialog_edit)
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.mipmap.add_icon)
            .setTitle("增加商品类别")
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                val content = editText.text.toString().trim()
                if (content.isEmpty()) {
                    toast { "请填写必须内容！！" }
                } else {
                    viewModelGoodsTo!!.addCategory(content)
                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }
}