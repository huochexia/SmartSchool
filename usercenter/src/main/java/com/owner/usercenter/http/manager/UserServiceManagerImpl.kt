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
import com.owner.usercenter.http.entities.LoginResp
import com.owner.usercenter.http.entities.RegisterUserReq
import com.owner.usercenter.http.entities.RegisterUserResp
import com.owner.usercenter.http.service.UserApi
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

/**
 *
 * Created by Liuyong on 2019-04-06.It's smartschool
 *@description:
 */
class UserServiceManagerImpl(
    private val service: UserApi
) : UserServiceManager {
    /*
      登录
     */
    override fun loginManager (username: String, password: String): Flowable<Either<Errors, LoginResp>> {
        // 如果登录成功，将LoginResp赋给Either函子的右值，左值为nothing，
        // 否则将错误赋值给函子的左值,右值为nothing。
        //这样将数据源得到数据转变为Either对象，输出给下游。
        return service.login(username, password)
            .subscribeOn(Schedulers.io())
            .map {
                if (it.isSuccess()) {
                    Either.right(it)
                } else {
                    Either.left(Errors.BmobError(it.code))
                }
            }
    }

    /*
     注册
     */
    override fun RegisterManager(username: String, mobilephone: String): Flowable<Either<Errors, RegisterUserResp>> {
        val newUser = RegisterUserReq(
            username = username,
            mobilePhoneNumber = mobilephone
        )
        return service.signUp(newUser)
            .subscribeOn(Schedulers.io())
            .map {
                if (it.isSuccess()) {
                    Either.right(it)
                } else {
                    Either.left(Errors.BmobError(it.code))
                }
            }
    }
}