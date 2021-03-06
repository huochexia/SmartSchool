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
 * 注册
 * Created by Liuyong on 2019-04-07.It's smartschool
 *@description:
 */

data class RegisterUserReq(
    val username: String,
    val mobilePhoneNumber: String,
    val password: String = "111111",
    val letters:String,
    val role:String,
    val district:Int = 0,
    val categoryCode:String ="-1"
)

/*
  只需要得到是否成功就行
 */
data class RegisterUserResp(
    val code: Int = 0,
    val error: String? = null,
    val objectId: String? = null

) {
    fun isSuccess(): Boolean = code == 0 && objectId.isNullOrEmpty().not()
}

/*
 商品类别
 */
data class CategoryResp(
    val objectId: String,
    val categoryName: String
)