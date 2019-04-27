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

import com.owner.basemodule.base.mvi.IIntent
import com.owner.usercenter.http.entities.LoginUser

/**
 *  登录界面所有意图：检查用户是否过期，自动登录，找回密码，设置是否自动登录，点击登录
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */
sealed class LoginIntent : IIntent {

    //初始化工作，主要是得到本地保存的用户信息，这个信息用于检查用户登录是否过期，
    // 如果没过期可以用于自动登录
    object InitialIntent : LoginIntent()


    //找回密码
    object FindPassWordIntent : LoginIntent()

    //选择是否自动登录
    data class SetAutoLoginIntent(val isAutoLogin: Boolean?) : LoginIntent()

    //点击按钮登录
    data class LoginClicksIntent(
        val mobilePhone: String?,
        val password: String?
    ) : LoginIntent()

}