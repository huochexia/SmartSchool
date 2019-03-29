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
package com.owner.basemodule.base.view.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

/**
 * 定义基础ViewModel工厂类，它的参数是一个返回具体ViewModel对象的Lambda表达式。
 * Created by Liuyong on 2019-03-21.It's smartschool
 *@description:
 */
class BaseViewModelFactory<T>(val creator: () -> T) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

/**
 * 扩展Fragment方法，获取ViewModel对象。默认参数值为null，用于ViewModel对象本身没有参数的情况。
 *
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null){
        ViewModelProviders.of(this).get(T::class.java)
    }else{
        ViewModelProviders.of(this,BaseViewModelFactory(creator)).get(T::class.java)
    }
}

/**
 *扩展Activity方法，获取ViewModel对象
        */
inline fun <reified T:ViewModel> AppCompatActivity.getViewModel(noinline creator:(()->T)? = null):T{
    return if (creator == null) {
        ViewModelProviders.of(this).get(T::class.java)
    } else {
        ViewModelProviders.of(this,BaseViewModelFactory(creator)).get(T::class.java)
    }
}