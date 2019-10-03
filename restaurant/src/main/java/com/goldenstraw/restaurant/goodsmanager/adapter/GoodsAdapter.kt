package com.goldenstraw.restaurant.goodsmanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.owner.basemodule.room.entities.Goods

/**
 * 商品列表定义为var类型，是因为该变量可以被从外部改变，改变后刷新
 */
class GoodsAdapter(
    private val goodsList: MutableList<Goods>
) : RecyclerView.Adapter<GoodsViewHolder>() {

    /*
     *改变列表内容,清空原内容，加入新内容，刷新列表
     */
    fun updateList(list: List<Goods>) {
        goodsList.clear()
        goodsList.addAll(list)
        notifyDataSetChanged()
        
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsViewHolder {
        return GoodsViewHolder.create(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int = goodsList.size

    override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) {
        holder.bindToData(goodsList[position])

    }

}