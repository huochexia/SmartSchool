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
package com.owner.basemodule.binding.support

import android.view.MenuItem
import android.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.owner.basemodule.functional.Consumer

/**
 * Toolbar与菜单项进行绑定。给Toolbar增加菜单属性，直接将属性值与
 * Created by Liuyong on 2019-03-24.It's smartschool
 *@description:
 */
@BindingAdapter("bind_menuClick")
fun onToolbarMenuClick(
    toolbar: Toolbar,
    consumer: Consumer<MenuItem>
) {
    toolbar.setOnMenuItemClickListener {
        consumer.accept(it)
        true
    }
}