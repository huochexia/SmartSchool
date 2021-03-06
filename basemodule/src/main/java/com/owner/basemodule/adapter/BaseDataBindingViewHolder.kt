package com.owner.basemodule.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * 1、将绑定视图做为参数，由Adapter传入
 * 2、将对数据与视图的绑定工作定义为回调方法。
 */
open class BaseDataBindingViewHolder<T : Any, DB : ViewDataBinding>(
    view:View,
    dataBinding: (View)->DB,
    private val callback: (T, DB, Int) -> Unit = { _, _, _ -> }
) : RecyclerView.ViewHolder(view) {

    private var binding: DB? = null

    init {
        binding = dataBinding(view)
    }

    fun bindToData(data: T, position: Int) {
        callback(data, binding!!, position)

        binding!!.executePendingBindings()
    }
}