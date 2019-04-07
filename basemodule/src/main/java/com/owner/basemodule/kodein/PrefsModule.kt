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
package com.owner.basemodule.kodein

import android.content.Context
import android.content.SharedPreferences
import com.owner.basemodule.base.BaseApplication
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

/**
 * 本地共享文件操作类注入
 * Created by Liuyong on 2019-04-05.It's smartschool
 *@description:
 */

private const val PREFS_MODULE_TAG = "PrefsModule"

private const val PREFS_MODULE_SP_TAG ="PrefsDefault"

val prefsModule = Kodein.Module(PREFS_MODULE_TAG){

    //给PrefsHelper提供实例
    bind<SharedPreferences>(PREFS_MODULE_SP_TAG) with singleton {
        BaseApplication.getInstance().getSharedPreferences(PREFS_MODULE_SP_TAG, Context.MODE_PRIVATE)
    }

}