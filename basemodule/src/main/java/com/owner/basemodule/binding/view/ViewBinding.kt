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
package com.owner.basemodule.binding.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.BindingAdapter
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.longClicks
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

/**
 * 自定义的一些常用的视图属性与设置方法的绑定适配器
 * Created by Liuyong on 2019-03-23.It's smartschool
 *@description:
 */
/**
 * 点击事件的抽象表示,具体方法由ViewModel来实现，做为实参传入高阶函数
 */
interface ViewClickConsumer : Consumer<View>

const val DEFAULT_THROTTLE_TIME = 500L //防抖时间间隔

/**
 * 绑定设置视图是否可见属性的方法
 */
@BindingAdapter("bind_view_visibility")
fun setVisible(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

/**
 * 对视图的长按事件，使用了RxBinding,将事件转换成可观察的数据对象
 */
@SuppressLint("CheckResult")
@BindingAdapter("bind_view_onLongClick")
fun setOnLongClickEvent(view: View, consumer: ViewClickConsumer) {

    view.longClicks()
        .subscribe { consumer.accept(view) }
}

/**
 * 单击事件绑定，同时增加了防抖功能属性。因为有些点击事件不需要防抖设置，所以此处将其设为可选
 */
@SuppressLint("CheckResult")
@BindingAdapter("bind_view_onClick", "bind_view_throttleFirst", requireAll = false)
fun setOnClickEvent(view: View, consumer: ViewClickConsumer, time: Long?) {

    view.clicks()
        .throttleFirst(time ?: DEFAULT_THROTTLE_TIME,TimeUnit.MILLISECONDS)
        .subscribe{consumer.accept(view)}
}

/**
 * 自定义属性，当点击它时关闭输入框。
 * 这个是与该属性绑定的设置方法
 */
@SuppressLint("CheckResult")
@BindingAdapter("bind_view_onClick_closeSoftInput")
fun closeSoftInputWhenClicked(view: View, closed: Boolean = false) {

    view.clicks()
        .subscribe{
            if (closed) {
                //获取系统输入方法管理器
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken,0)
            }
        }
}