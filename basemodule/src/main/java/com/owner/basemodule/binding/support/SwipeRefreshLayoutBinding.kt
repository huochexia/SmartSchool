package com.owner.basemodule.binding.support

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * 双向绑定下拉刷新。因为这里的适配器是在类当中，所以需要@JvmStatic对Java表示该方法为静态方法
 * Created by Liuyong on 2019-03-23.It's smartschool
 *@description:
 */
object SwipeRefreshLayoutBinding {

    /**
     *正向绑定，数据到显示,通知视图是否刷新
     */
    @JvmStatic
    @BindingAdapter("app:bind_swipeRefreshLayout_refreshing")
    fun setSwipeRefreshLayoutRefreshing(
        swipeRefreshLayout: SwipeRefreshLayout,
        newValue: Boolean
    ) {
        //因为是双向绑定，避免出现死循环修改，所以只有当新值与旧值不一样时才操作
        if (swipeRefreshLayout.isRefreshing != newValue) {
        }
        swipeRefreshLayout.isRefreshing = newValue
    }

    /**
     * 逆向绑定监听器，如果视图改变数据。由它将结果返回给数据层（ViewModel）。
     * 适配器的attribute值指的是视图绑定的属性名，它与正向绑定的要一致。
     * event值要与反向保存数据适配器的值一致，名称后缀“AttrChanged”
     *
     */
    @JvmStatic
    @InverseBindingAdapter(
        attribute = "app:bind_swipeRefreshLayout_refreshing",
        event = "app:bind_swipeRefreshLayout_refreshingAttrChanged"
    )
    fun isSwipeRefreshLayoutRefreshing(swipeRefreshLayout: SwipeRefreshLayout): Boolean =
        swipeRefreshLayout.isRefreshing

    /**
     * 视图事件监听器注册适配器，事件监听与逆向绑定监听绑定在这个适配器上，
     * 一旦事件发生，调用逆向绑定监听器的onChange方法。
     */
    @JvmStatic
    @BindingAdapter("app:bind_swipeRefreshLayout_refreshingAttrChanged", requireAll = false)
    fun setOnRefreshListener(swipeRefreshLayout: SwipeRefreshLayout, bindingListener: InverseBindingListener?) {

        bindingListener?.let {
            //定义事件监听，下拉刷新监听
            swipeRefreshLayout.setOnRefreshListener {
                bindingListener.onChange()//必须调用这个方法，因为该方法中将绑定监听器返的值赋值给绑定的属性变量。
            }
        }
    }
}