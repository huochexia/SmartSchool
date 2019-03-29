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

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 本地共享资源存储
 * Created by Liuyong on 2019-03-25.It's smartschool
 *@description:
 */

class PrefsDelegate<T>(
    private val context: Context,
    private val key: String,
    private val default: T,
    private val presFileName: String = "smart_school"
) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
       return getPreference(key)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
         putPreference(key,value)
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(presFileName, Context.MODE_PRIVATE)
    }

    private fun getPreference(key: String): T {
        return when (default) {
            is String -> prefs.getString(key, default)
            is Long -> prefs.getLong(key, default)
            is Int -> prefs.getInt(key, default)
            is Boolean -> prefs.getBoolean(key, default)
            is Float -> prefs.getFloat(key, default)
            else -> throw IllegalArgumentException("Unknown Type.")
        } as T
    }

    private fun putPreference(key: String, value: T) {
        with(prefs.edit()) {
            when (value) {
                is String -> putString(key,value)
                is Long -> putLong(key,value)
                is Float -> putFloat(key,value)
                is Int -> putInt(key,value)
                is Boolean -> putBoolean(key,value)
                else -> throw java.lang.IllegalArgumentException("Unknown Type")
            }
        }.apply()
    }
}