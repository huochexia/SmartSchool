package com.goldenstraw.restaurant.goodsmanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.owner.basemodule.adapter.BaseDataBindingViewHolder
import com.owner.basemodule.room.entities.Goods

class PageDataBindingAdapter<DB : ViewDataBinding>(
    private val layoutId: Int,//item的layoutId
    private val dataBinding: (View) -> DB,
    private val callback: (Goods, DB, Int) -> Unit = { _, _, _ -> }

) : PagedListAdapter<Goods, BaseDataBindingViewHolder<Goods, DB>>(object :
    DiffUtil.ItemCallback<Goods>() {

    override fun areItemsTheSame(oldItem: Goods, newItem: Goods): Boolean =
        oldItem.objectId == newItem.objectId

    override fun areContentsTheSame(oldItem: Goods, newItem: Goods): Boolean =
        oldItem == newItem

}) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseDataBindingViewHolder<Goods, DB> = BaseDataBindingViewHolder(
        LayoutInflater.from(parent.context).inflate(layoutId, parent, false),
        dataBinding,
        callback
    )

    override fun onBindViewHolder(holder: BaseDataBindingViewHolder<Goods, DB>, position: Int) {
         holder.bindToData(getItem(position)!!, position)
    }

    fun forceUpdate() {
        notifyDataSetChanged()
    }

}