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
package com.owner.basemodule.functional

/**
 * 功能接口：抽象的表示一些格式相同的方法，将它做为形参。
 * Created by Liuyong on 2019-03-23.It's smartschool
 *@description:
 */

/**
 * 使用者：一个参数，表示要对该参数进行操作，无返回值的方法。
 * java.util包中也有此接口，但是它需要API24版本以上。io.reactivex.function包中也含有此接口
 * 功能相同
 */
interface Consumer<T> {
    fun accept(t:T)
}

/**
 *  提供者：表示无参数，有返回值的表达式的别名，
 */
typealias Supplier<T> = ()->T