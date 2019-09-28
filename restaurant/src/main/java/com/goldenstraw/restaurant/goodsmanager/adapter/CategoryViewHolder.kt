package com.goldenstraw.restaurant.goodsmanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.*
import androidx.recyclerview.widget.RecyclerView
import com.goldenstraw.restaurant.databinding.LayoutGoodsCategoryBinding
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CategoryViewModel
import com.owner.basemodule.adapter.BaseDataBindingViewHolder
import com.owner.basemodule.room.entities.GoodsCategory
import kotlinx.android.synthetic.main.layout_goods_item.view.*

/**
 * 商品类别列表ViewHolder
 */
class CategoryViewHolder(
    val mBinding: LayoutGoodsCategoryBinding
) : RecyclerView.ViewHolder(mBinding.root) {
    /*
    伴生对象，相当于Java中的静态方法，用于生成一个ViewHolder对象
     */
    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup): CategoryViewHolder {
            val binding = LayoutGoodsCategoryBinding.inflate(inflater, parent, false)
            return CategoryViewHolder(binding)
        }
    }

    fun bindToData(category: GoodsCategory) {
        if (mBinding.categoryVM == null) {
            mBinding.categoryVM = CategoryViewModel(category)
        } else {
            mBinding.categoryVM.category = category
        }

        mBinding.executePendingBindings()
    }
}