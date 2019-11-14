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
package com.owner.usercenter.http.manager

import arrow.core.Either
import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.room.entities.AllUserResp
import com.owner.usercenter.http.entities.*
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * 这是个中介接口，汇聚所有对用户的操作方法，分别对应UserService实例方法。
 * 在它的实现类中对得到的数据源进行必要的操作，得到数据流下游所要的数据。
 * Created by Liuyong on 2019-04-06.It's smartschool
 *@description:
 */
interface UserServiceManager {
    /**
     * 登录，上游数据是LoginResp。因为下游数据需要区分开正确的和错误的两种，所以在这里进行区分。
     */
    fun loginManager(mobilephone:String,password:String): Flowable<Either<Errors,LoginResp>>

    /**
     * 检查用户是否过期
     */
    fun checkLogin(sessionToken:String,objectId:String):Single<Either<Errors,Boolean>>


    /**
     * 注册，将API返回的结果进行分类（成功或失败）处理，发送一个Either结果给下游
     */
    fun registerManager(username:String,mobilephone:String):Flowable<Either<Errors,RegisterUserResp>>

    /**
     * 更换密码
     */
    fun changePwd(sessionToken:String, objectId:String, oldPassword:String, newPassword:String):Flowable<Either<Errors,ChangePwdResp>>
    /**
     * 请求验证码
     */
    fun requestSmsCode(mobilephone: String):Single<RequestCodeResp>

    /**
     * 验证验证码
     */
    fun verifySmsCode(mobilephone: String,smsId:String):Single<VerifyCodeResp>
    /**
     * 重置密码
     */
    fun resetPwd(newPassword:String,smsCode:String):Single<ResetPasswordResp>

    /**
     * 获取所有用户信息
     */
    fun getAllUsers(): Flowable<AllUserResp>

}