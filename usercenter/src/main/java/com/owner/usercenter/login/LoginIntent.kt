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

/**
 *  登录界面所有意图：初始化，登录，找回密码
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */
sealed class LoginIntent : IIntent {

    object InitialIntent : LoginIntent()
    //找回密码
    object FindPassWordIntent : LoginIntent()
    //选择是否自动登录
    data class SetAutoLoginIntent(val isAutoLogin: Boolean?) : LoginIntent()

    //点击按钮登录
    data class LoginClicksIntent(
        val username: String?,
        val password: String?
    ) : LoginIntent()

}