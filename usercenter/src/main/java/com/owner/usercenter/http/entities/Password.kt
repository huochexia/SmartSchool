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
 *  修改密码
 * Created by Liuyong on 2019-04-12.It's smartschool
 *@description:
 */

data class ChangePwdReq(
    val oldPassword: String,
    val newPassword: String
)

data class ChangePwdResp(
    val code: Int = 0,
    val error: String?,
    val msg: String?
) {
    fun isSuccess(): Boolean = code == 0
}

/**
 * 重置密码
 */
data class ResetPasswordReq(
    val password: String
)

data class ResetPasswordResp(
    val code: Int = 0,
    val error: String?,
    val msg: String?
) {
    fun isSuccess() = code == 0
}