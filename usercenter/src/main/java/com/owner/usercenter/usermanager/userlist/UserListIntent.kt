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
package com.owner.usercenter.usermanager.userlist

import com.owner.basemodule.base.mvi.IIntent

/**
 *
 * Created by Liuyong on 2019-04-30.It's smartschool
 *@description:
 */
sealed class UserListIntent : IIntent {

    object initIntent : UserListIntent() //初始化，用于获取所有用户

    data class searchUserIntent(val username:String):UserListIntent() //通过用户名搜索用户

}