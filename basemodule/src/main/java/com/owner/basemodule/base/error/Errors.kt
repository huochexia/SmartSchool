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
package com.owner.basemodule.base.error

/**
 * 基本错误类,将错误细分为几种类
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */
sealed class Errors : Throwable() {
    //网络错误
    object NetworkError : Errors()

    //空输入
    object EmptyInputError : Errors()

    //空结果
    object EmptyResultsError : Errors()

    //用于输出错误信息
    data class SimpleMessageError(val simpleMessage: String) : Errors()

    //系统异常
    data class ErrorWrapper(val errors: Throwable) : Errors()

    //Bmob访问数据错误
    data class BmobError(val code: Int) : Errors() {
        val errorMessage: String
            get() = when (code) {
                101 -> "用户名或密码不正确"
                202 -> "用户已存在"
                203 -> "邮箱已存在"
                205 -> "没有此用户"
                206 -> "登录用户才能修改自己的信息"
                207 -> "验证码错误"
                209 -> "该手机号码已存在"
                210 -> "旧密码不正确"
                301 -> "手机号码必须是11位的数字"
                else -> "未知错误类型"
            }
    }
}