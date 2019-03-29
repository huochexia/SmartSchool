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
package com.owner.basemodule.binding.design

import android.view.MenuItem
import androidx.databinding.BindingAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.functions.Consumer


/**
 * 底部导航选择事件与底部导航属性绑定的适配器
 * Created by Liuyong on 2019-03-23.It's smartschool
 *@description:
 */

interface SelectedChangeConsumer : Consumer<MenuItem>

@BindingAdapter("bind_onNavigationBottomSelectedChanged")
fun setOnSelectedChangeListener(
    view: BottomNavigationView,
    consumer: SelectedChangeConsumer?
) {
    view.setOnNavigationItemSelectedListener { item: MenuItem ->
        consumer?.accept(item)
        true
    }
}