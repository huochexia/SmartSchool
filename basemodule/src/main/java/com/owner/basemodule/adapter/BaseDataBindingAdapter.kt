package com.owner.basemodule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView的基本适配器：
 * @dataSource 数据源
 * @dataBinding item的绑定实现类
 * @callback   绑定数据和方法，这个函数需要三个参数，数据项，数据项所绑定的视图和数据项所在位置。
 * @onMove   拖拽事件方法
 * @onItemDismiss  侧滑事件方法
 */
open class BaseDataBindingAdapter<T : Any, DB : ViewDataBinding>(
    private val layoutId: Int,//item的layoutId
    private val dataSource: () -> List<T>,
    private val dataBinding: (View) -> DB,
    private val callback: (T, DB, Int) -> Unit = { _, _, _ -> }

) : RecyclerView.Adapter<BaseDataBindingViewHolder<T, DB>>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseDataBindingViewHolder<T, DB> = BaseDataBindingViewHolder(
        LayoutInflater.from(parent.context).inflate(layoutId, parent, false),
        dataBinding,
        callback
    )

    override fun getItemCount(): Int = dataSource().size


    override fun onBindViewHolder(holder: BaseDataBindingViewHolder<T, DB>, position: Int) {
        holder.bindToData(dataSource()[position], position)
    }

    fun forceUpdate() {
        notifyDataSetChanged()
    }

}