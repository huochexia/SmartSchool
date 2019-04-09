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
package com.owner.usercenter.register

import com.owner.basemodule.base.mvi.IIntent

/**
 * 注册新用户时的意图：初始化意图，点击注册按钮意图
 * Created by Liuyong on 2019-04-07.It's smartschool
 *@description:
 */
sealed class RegisterIntent : IIntent {
    /*
     初始化意图，用于清空输入框
     */
    object InitialRegisterIntent:RegisterIntent()
    /*
      点击注册按钮意图
     */
    data class ClickRegisterIntent(
        val username: String?, val mobilephone: String?
    ) : RegisterIntent()
}