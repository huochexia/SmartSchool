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

import com.owner.basemodule.room.entities.AllUserResp
import com.owner.usercenter.http.entities.*
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.*

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
    fun login(@Query("username") mobilePhoneNumber: String, @Query("password") password: String)
            : Flowable<LoginResp>

    /*
     *检查用户的登录是否过期
     */
    @GET("1/checkSession/{objectID}")
    fun check(
        @Header("X-Bmob-Session-Token") sessionToken: String,
        @Path("objectID") objectId: String
    ): Single<CheckLoginUser>

    /*
     *注册用户
     */
    @POST("/1/users")
    fun signUp(@Body newUser: RegisterUserReq): Flowable<RegisterUserResp>

    /*
     *更换密码
     */
    @PUT("/1/updateUserPassword/{objectId}")
    fun changePwd(
        @Header("X-Bmob-Session-Token") token: String,
        @Path("objectId") objectId: String,
        @Body newPwd: ChangePwdReq
    ): Flowable<ChangePwdResp>

    /*
     * 请求短信验证码
     */
    @POST("/1/requestSmsCode")
    fun requestSmsCode(@Body request: RequestCodeReq): Single<RequestCodeResp>

    /*
     * 验证短信验证码
     */
    @POST("/1/verifySmsCode/{smsCode}")
    fun verifySmsCode(@Body verify: VerifyCodeReq, @Path("smsCode") smsCode: String): Single<VerifyCodeResp>

    /*
     * 重置密码
     */
    @PUT("/1/resetPasswordBySmsCode/{smsCode}")
    fun resetPassword(@Body newPwd: ResetPasswordReq, @Path("smsCode") smsCode: String): Single<ResetPasswordResp>

    /*
     *查询所有用户
     */
    @GET("/1/users")
    fun getAllUsers(): Flowable<AllUserResp>


}

