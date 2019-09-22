package com.owner.basemodule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class BaseDataBindingAdapter<T : Any, DB : ViewDataBinding>(
    private val dataSource: () -> List<T>,
    private val dataBinding: DB,
    private val callback: (T, DB, Int) -> Unit = { _, _, _ -> }
) : RecyclerView.Adapter<BaseDataBindingViewHolder<T, DB>>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseDataBindingViewHolder<T, DB> = BaseDataBindingViewHolder(
        dataBinding,
        callback,
        viewType
    )

    override fun getItemCount(): Int = dataSource().size


    override fun onBindViewHolder(holder: BaseDataBindingViewHolder<T, DB>, position: Int) =
        holder.bindToData(dataSource()[position], position)

    fun forceUpdate() {
        notifyDataSetChanged()
    }
}