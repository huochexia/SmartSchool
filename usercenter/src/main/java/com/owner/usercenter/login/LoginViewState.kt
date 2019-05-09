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

import com.owner.usercenter.mvi.IViewState
import com.owner.usercenter.http.entities.LoginResp
import com.owner.usercenter.http.entities.LoginUser

/**
 *登录界面ViewState。因为登录主要是对登录事件的处理，完成界面的导航，所以这里的状态主要是事件类
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */
data class LoginViewState(
    val isLoading: Boolean,
    val errors: Throwable?,
    val uiEvents: LoginUiEvent?
) : IViewState {
    /**
     * 通过个事件对象，UI进行处理相应事件
     */
    sealed class LoginUiEvent {

        data class CheckEvent(val loginUser: LoginUser, val autoLogin: Boolean) : LoginUiEvent()

        data class JumpMain(val loginUser: LoginResp) : LoginUiEvent()

        data class SetAutoLoginInfo(val isAutoLogin: Boolean?) : LoginUiEvent()

        object JumpFindPassWord :LoginUiEvent()

        data class TryAutoLogin(
            val loginEntity: LoginUser,
            val autoLogin: Boolean
        ) : LoginUiEvent()
    }

    companion object {
        /**
         * 空闲状态
         */
        fun idle(): LoginViewState = LoginViewState(
            isLoading = false,
            errors = null,
            uiEvents = null
        )
    }
}