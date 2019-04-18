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

import com.owner.basemodule.base.mvi.IAction

/**
 * 登录界面Action：检查用户是否过期，登录，找回密码，设置是否自动登录
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */
sealed class LoginAction :IAction{

    object InitialAction : LoginAction()

    data class SetAutoLoginAction(val isAutoLogin:Boolean?):LoginAction()

    object FindPassWordAction : LoginAction()

    data class ClickLoginAction(
        val username:String?,
        val password :String?
    ):LoginAction()


}