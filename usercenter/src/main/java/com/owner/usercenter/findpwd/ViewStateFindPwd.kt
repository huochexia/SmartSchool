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
package com.owner.usercenter.findpwd

import com.owner.basemodule.base.mvi.IViewState

/**
 *
 * Created by Liuyong on 2019-04-21.It's smartschool
 *@description:
 */
data class ViewStateFindPwd(
    val error: Throwable?,
    val uiEvent: FindPwdEvent?
) : IViewState {

    sealed class FindPwdEvent {
        //发送短信
        data class sendSmsCode(val smsCode:String):FindPwdEvent()
        //跳转到重置密码界面
        data class jumpReset(val smsCode:String) : FindPwdEvent()

    }

    companion object {
        fun idle(): ViewStateFindPwd {
            return ViewStateFindPwd(
                error = null,
                uiEvent = null
            )
        }
    }
}