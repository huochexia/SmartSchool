package com.goldenstraw.restaurant.goodsmanager.adapter

import android.view.View
import com.goldenstraw.restaurant.databinding.FragmentGoodsListBinding
import com.owner.basemodule.adapter.BaseDataBindingViewHolder
import com.owner.basemodule.room.entities.Goods

/**
 * 商品列表ViewHolder
 */
class GoodsViewHolder(
    dataBinding: FragmentGoodsListBinding,
    callback: (Goods, FragmentGoodsListBinding, Int) -> Unit = { _, _, _ -> },
    viewType: Int
) : BaseDataBindingViewHolder<Goods, FragmentGoodsListBinding>(
    dataBinding, callback, viewType
)