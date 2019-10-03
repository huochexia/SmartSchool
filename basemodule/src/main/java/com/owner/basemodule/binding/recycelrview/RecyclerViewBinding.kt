/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.owner.basemodule.binding.recycelrview

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.recyclerview.scrollStateChanges
import com.kennyc.view.MultiStateView
import java.util.concurrent.TimeUnit

/**
 * 对RecyclerView的自定义属性与设置方法的绑定适配器
 * Created by Liuyong on 2019-03-23.It's smartschool
 *@description:
 */

/**
 * 为RecyclerView绑定适配器
 */
@BindingAdapter("bind_recyclerView_adapter")
fun setRecyclerViewAdapter(
    recyclerView: RecyclerView,
    adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?
) {
    adapter?.apply {
        recyclerView.adapter = this //将传入的Adapter赋值于recyclerView
    }
}

/**
 * 绑定滚动事件，这里使用了RxRecyclerView的scrollStateChanges(),
 * 其实对应的是onScrollStateChanged（）这个方法。 它有三种状态:
 * 1、SCROLL_STATE_IDLE = 0 ; 静止没有滚动
 * 2、SCROLL_STATE_DRAGGING = 1 ;正在被外部拖拽，一般为用户正在用手指滚动
 * 3、SCROLL_STATE_SETTING = 2；自动滚动
 */
@SuppressLint("CheckResult")
@BindingAdapter(
    "bind_recyclerView_scrollStateChanges",
    "bind_recyclerView_scrollStateChanges_debounce", requireAll = false
)
fun setScrollStateChanges(
    recyclerView: RecyclerView,
    listener: (Int) -> Int,
    debounce: Long = 500
) {
    recyclerView.scrollStateChanges()
        .debounce(debounce, TimeUnit.MILLISECONDS)
        .subscribe {
            listener(it)
        }
}

/**
 * 多状态属性设置
 */
@BindingAdapter("msv_viewState")
fun setViewState(viewState: MultiStateView, state: Int) {
    viewState.viewState = state
}