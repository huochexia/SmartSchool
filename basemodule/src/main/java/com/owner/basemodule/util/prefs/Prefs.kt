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
package com.owner.basemodule.util.prefs

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 本地共享资源存储,SharedPreferences扩展
 * Created by Liuyong on 2019-03-25.It's smartschool
 *@description:
 */
private inline fun <T> SharedPreferences.delegate(
    key: String? = null,
    defaultValue: T,
    crossinline getter: SharedPreferences.(String, T) -> T,
    crossinline setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor
): ReadWriteProperty<Any, T> =
    object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            //如果key为null,则使用要定义的属性的名称
            return getter(key ?: property.name, defaultValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            //调用SharedPreferences的edit()方法得到Editor
            edit().setter(key ?: property.name, value).apply()
        }

    }

/**
 * Int数据读写的代理类
 */
fun SharedPreferences.int(key: String? = null, defValue: Int = 0): ReadWriteProperty<Any, Int> {
    return delegate(key, defValue, SharedPreferences::getInt, SharedPreferences.Editor::putInt)
}

/**
 * 得到Long类型数据读写的代理类
 */
fun SharedPreferences.long(key: String? = null, defValue: Long = 0): ReadWriteProperty<Any, Long> {
    return delegate(key, defValue, SharedPreferences::getLong, SharedPreferences.Editor::putLong)
}

/**
 * 得到Float类型数据读写的代理类
 */
fun SharedPreferences.float(key: String? = null, defValue: Float = 0f): ReadWriteProperty<Any, Float> {

    return delegate(key,defValue,SharedPreferences::getFloat, SharedPreferences.Editor::putFloat)
}
/**
 * 得到Boolean类型数据读写的代理类
 */
fun SharedPreferences.boolean(key:String?=null,defValue:Boolean = false):ReadWriteProperty<Any,Boolean>{

    return delegate(key,defValue,SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)
}

/**
 * 得到String类型数据读写的代理类
 */
fun SharedPreferences.string(key: String? = null, defValue: String = ""): ReadWriteProperty<Any,String> {

    return delegate(key,defValue,SharedPreferences::getString, SharedPreferences.Editor::putString)
}

/**
 * 得到Set<String>类型数据读写的代理类
 */
fun SharedPreferences.stringSet(
    key: String? = null,
    defValue: Set<String> = emptySet()
): ReadWriteProperty<Any, Set<String>> {
    return delegate(key,defValue,SharedPreferences::getStringSet,SharedPreferences.Editor::putStringSet)
}

