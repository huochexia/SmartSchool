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
package com.owner.basemodule.logger

import android.app.Application
import com.owner.basemodule.functional.Supplier
import timber.log.Timber
import kotlin.math.log

/**
 *
 * Created by Liuyong on 2019-03-24.It's smartschool
 *@description:
 */
fun Application.initLogger(isDebug: Boolean = true) {
    if (isDebug)
        Timber.plant(Timber.DebugTree())
    else
        Timber.plant(CrashReportingTree())

    logd{"initLogger successfully,isDebug = $isDebug"}
}


inline fun logd(supplier:Supplier<String>) = Timber.d(supplier())

inline fun logi(supplier: Supplier<String>) = Timber.i(supplier())

inline fun logw(supplier:Supplier<String>) = Timber.w(supplier())

inline fun loge(supplier:Supplier<String>) = Timber.e(supplier())