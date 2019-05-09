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
package com.owner.basemodule.util

import android.content.Context
import android.widget.Toast
import com.owner.basemodule.base.BaseApplication

/**
 * 定义上下文的扩展方法
 * Created by Liuyong on 2019-05-06.It's smartschool
 *@description:
 */
inline fun Context.toast(value: () -> String) =
    Toast.makeText(this, value(), Toast.LENGTH_SHORT).show()

fun Context.toast(value: String) = toast { value }

inline fun toast(value: () -> String): Unit =
    BaseApplication.getInstance().toast(value)