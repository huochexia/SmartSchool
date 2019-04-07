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
package com.owner.usercenter.login

import com.owner.basemodule.base.mvi.IResult
import com.owner.usercenter.http.entities.LoginReq
import com.owner.usercenter.http.entities.LoginResp

/**
 *登录界面的Result：自动登录，点击登录，找回密码。
 * 自动登录：成功，失败，没有用户信息（指本地没有用户信息），InFlight
 * 点击登录：成功，失败，InFlight
 * 找回密码：
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */
sealed class LoginResult : IResult {

    //进行自动登录可能产生的结果
    sealed class AutoLoginInfoResult : LoginResult() {
        data class Success(val user: LoginReq, val autoLogin: Boolean) : AutoLoginInfoResult()
        data class Failure(val error: Throwable) : AutoLoginInfoResult()

        object NoUserData : AutoLoginInfoResult()
        object InFlight : AutoLoginInfoResult()
    }

    //保存是否自动登录信息
    data class SetAutoLoginInfoResult(val isAutoLogin: Boolean?) : LoginResult()

    //找回密码
    object FindPassWordResult :LoginResult()

    //点击登录后可能产生的结果
    sealed class ClickLoginResult : LoginResult() {
        data class Success(val user: LoginResp) : ClickLoginResult()
        data class Failure(val error: Throwable) : ClickLoginResult()

        object InFlight : ClickLoginResult()
    }


}