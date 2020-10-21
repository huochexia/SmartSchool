package com.owner.basemodule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

/**
 * 分页适配器，封装DataBinding
 */
class BasePageDataBindingAdapter<T : Any, DB : ViewDataBinding>(
    private val layoutId: Int,//item的layoutId
    private val dataBinding: (View) -> DB,
    private val callback: (T, DB, Int) -> Unit = { _, _, _ -> },
    private val diffCallback: DiffUtil.ItemCallback<T>

) : PagingDataAdapter<T, BaseDataBindingViewHolder<T, DB>>(diffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseDataBindingViewHolder<T, DB> = BaseDataBindingViewHolder(
        LayoutInflater.from(parent.context).inflate(layoutId, parent, false),
        dataBinding,
        callback
    )

    override fun onBindViewHolder(holder: BaseDataBindingViewHolder<T, DB>, position: Int) {
        holder.bindToData(getItem(position)!!, position)
    }

    fun forceUpdate() {
        notifyDataSetChanged()
    }

}