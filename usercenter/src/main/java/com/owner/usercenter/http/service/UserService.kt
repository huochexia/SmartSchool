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
package com.owner.usercenter.http.service

import com.owner.basemodule.network.RetrofitFactory
import com.owner.usercenter.http.entities.LoginResp
import com.owner.usercenter.http.entities.RegisterUserReq
import com.owner.usercenter.http.entities.RegisterUserResp
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *用户操作网络接口API，数据流的源
 * Created by Liuyong on 2019-04-03.It's smartschool
 *@description:
 */
interface UserApi {
    /*
     * 用户名(或手机号)和密码登录
     */
    @GET("1/login/")
    fun login(@Query("username") username: String, @Query("password") password: String)
            : Flowable<LoginResp>

    /*
     *注册用户
     */
    @POST("/1/users")
    fun signUp(@Body newUser: RegisterUserReq):Flowable<RegisterUserResp>
}

/**
 * 用户操作网络接口的实例----单例模式。
 */
object UserService : UserApi by RetrofitFactory.getInstance().create(UserApi::class.java)