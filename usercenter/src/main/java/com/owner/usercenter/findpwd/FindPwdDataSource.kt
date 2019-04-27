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

import arrow.core.Either
import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.base.repository.BaseRepositoryRemote
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.usercenter.http.entities.RequestCodeReq
import com.owner.usercenter.http.entities.RequestCodeResp
import com.owner.usercenter.http.entities.VerifyCodeReq
import com.owner.usercenter.http.entities.VerifyCodeResp
import com.owner.usercenter.http.service.UserApi
import io.reactivex.Single

/**
 *
 * Created by Liuyong on 2019-04-21.It's smartschool
 *@description:
 */

interface IRemoteSmsCodeDataSource : IRemoteDataSource {
    //获取验证码
    fun getSmsCode(mobilePhone: String): Single<RequestCodeResp>

    //验证验证码
    fun verifySmsCode(mobilePhone: String, smsCode: String): Single<VerifyCodeResp>
}

class RemoteSmsCodeDataSource(
    private val service: UserApi
) : IRemoteSmsCodeDataSource {

    override fun getSmsCode(mobilePhone: String): Single<RequestCodeResp> {
        val entity = RequestCodeReq(mobilePhone)
        return service.requestSmsCode(entity)
    }

    override fun verifySmsCode(mobilePhone: String, smsCode: String): Single<VerifyCodeResp> {
        val entity = VerifyCodeReq(mobilePhone)
        return service.verifySmsCode(entity, smsCode)
    }

}

class FindPwdRepositoryDataSource(
    private val remoteData: IRemoteSmsCodeDataSource
) : BaseRepositoryRemote<IRemoteSmsCodeDataSource>(remoteData) {

    fun getSmsCode(mobilePhone: String): Single<Either<Errors, RequestCodeResp>> {

        return remoteData.getSmsCode(mobilePhone).map {
            if (it.isSuccess()) {
                Either.right(it)
            } else {
                Either.left(Errors.BmobError(it.code))
            }
        }
    }

    fun verifySmsCode(mobilephone: String, smsCode: String): Single<Either<Errors, VerifyCodeResp>> {
        return remoteData.verifySmsCode(mobilephone, smsCode)
            .map {
                if (it.isSuccess()) {
                    Either.right(it)
                } else {
                    Either.left(Errors.BmobError(it.code))
                }
            }
    }
}