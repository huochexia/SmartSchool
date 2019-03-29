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

import android.util.Log
import timber.log.Timber

/**
 *
 * Created by Liuyong on 2019-03-24.It's smartschool
 *@description:
 */
class CrashReportingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        FakeCrashLibrary.log(priority, tag, message)

        if (t != null) {
            if (priority == Log.ERROR) {
                FakeCrashLibrary.logError(t)
            } else if (priority == Log.WARN) {
                FakeCrashLibrary.logWarning(t)
            }
        }
    }

}
/**
 * 这个不是真正的崩溃报告库，只是一个自定义样板，说明可以自定义一个Tree实例。
 */
private class FakeCrashLibrary private constructor() {
    init {
        throw AssertionError("No instances")
    }

    companion object {
        fun log(priority: Int, tag: String?, message: String) {
            //TODO 将日志条目添加到循环缓冲区
        }

        fun logWarning(t: Throwable) {
            //TODO 报告非致命的警告
        }

        fun logError(t: Throwable) {
            // TODO 非致命错误报告
        }
    }
}