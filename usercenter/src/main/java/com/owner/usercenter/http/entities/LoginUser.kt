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
package com.owner.usercenter.http.entities

/**
 * 请求登录
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */
data class LoginUser(
    val username: String,
    val password: String,
    val sessionToken: String,
    val objectId: String
)

/**
 * 响应登录
 */
data class LoginResp(val code :Int = 0,
                     val error:String?,
                     val sessionToken: String?,
                     val username: String?,
                     val mobilePhoneNumber: String?,
                     val objectId: String?,
                     val avatar:String?,
                     val emailVerified: Boolean,
                     val mobilePhoneVerified: Boolean){
    //因为自定义的拦截器中对404错误的响应结果时行了改写，将其body做为了成功200的body。
    // 所以当code不是0时，可以是404错误的body，此时需要提示用户错误信息。
    fun isSuccess() :Boolean = code == 0 && sessionToken.isNullOrEmpty().not()
}

/**
 * 检查用户的登录是否过期响应体
 */
data class CheckLoginUser(val msg: String)
