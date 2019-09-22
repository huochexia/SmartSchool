package com.goldenstraw.restaurant.goodsmanager.adapter

import android.view.View
import com.goldenstraw.restaurant.databinding.FragmentGoodsListBinding
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.room.entities.Goods

class GoodsAdapter(
    dataSource: () -> List<Goods>,
    dataBinding: FragmentGoodsListBinding,
    callback: (Goods, FragmentGoodsListBinding, Int) -> Unit = { _, _, _ -> }
) : BaseDataBindingAdapter<Goods, FragmentGoodsListBinding>(
    dataSource, dataBinding, callback
)
