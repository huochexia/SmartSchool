package com.goldenstraw.restaurant.goodsmanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goldenstraw.restaurant.databinding.LayoutGoodsCategoryBinding
import com.owner.basemodule.room.entities.GoodsCategory



class CategoryAdapter(
    var categoryList: List<GoodsCategory>
) : RecyclerView.Adapter<CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.create(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bindToData(categoryList[position])
    }


}