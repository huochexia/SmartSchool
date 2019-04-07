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
package com.owner.basemodule.ext

import android.widget.Toast
import com.owner.basemodule.base.BaseApplication
import com.owner.basemodule.logger.logw
import java.io.File

/**
 *通用扩展方法文件
 * Created by Liuyong on 2019-04-04.It's smartschool
 *@description:
 */
/*
 * File扩展方法：路径判断。
 * 如果不是子目录，判断是不是文件。如果是文件则删除后创建新的
 */
fun File.ensureDis(): Boolean {
    try {
        if (!isDirectory) {
            if (isFile) {
                delete()
            }
            return mkdirs()
        }
    } catch (e: Exception) {
        logw { "FileExt:+${e.message}" }
    }
    return false
}
