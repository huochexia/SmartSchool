package com.goldenstraw.restaurant.goodsmanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.goodsmanager.viewmodel.GoodsViewModel
import com.owner.basemodule.room.entities.Goods
import java.util.zip.Inflater

class GoodsViewHolder(
    val mBinding: LayoutGoodsItemBinding
) : RecyclerView.ViewHolder(mBinding.root) {

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup): GoodsViewHolder {
            val binding = LayoutGoodsItemBinding.inflate(inflater, parent, false)
            return GoodsViewHolder(binding)
        }
    }

    fun bindToData(goods: Goods) {

        mBinding.goods = goods
    }
}