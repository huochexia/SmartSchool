package com.goldenstraw.restaurant.goodsmanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.owner.basemodule.room.entities.Goods

class GoodsAdapter(
    private val goodsList: List<Goods>
) : RecyclerView.Adapter<GoodsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsViewHolder {
        return GoodsViewHolder.create(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int = goodsList.size

    override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) {
        holder.bindToData(goodsList[position])
    }
}