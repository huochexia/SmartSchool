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
package com.owner.usercenter.resetpwd

import arrow.core.Either
import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.base.repository.BaseRepositoryRemote
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.usercenter.http.entities.ResetPasswordResp
import com.owner.usercenter.http.manager.UserServiceManager
import io.reactivex.Single

/**
 *
 * Created by Liuyong on 2019-04-23.It's smartschool
 *@description:
 */

interface IResetRemoteDataSource : IRemoteDataSource {

    fun resetPassword(newPassword: String, smsCode: String): Single<ResetPasswordResp>
}

class ResetRemoteDataSource(
    val service: UserServiceManager
) : IResetRemoteDataSource {
    override fun resetPassword(newPassword: String, smsCode: String): Single<ResetPasswordResp> {
        return service.resetPwd(newPassword, smsCode)
    }
}

class ResetPwdRepository(
    remoteDataSource: ResetRemoteDataSource
) : BaseRepositoryRemote<IResetRemoteDataSource>(remoteDataSource) {

    fun resetPassword(newPassword: String, smsCode: String): Single<Either<Errors,ResetPasswordResp>> {

        return remoteDataSource.resetPassword(newPassword,smsCode).map {
            if (it.isSuccess()) {
                Either.right(it)
            }else{
                Either.left(Errors.BmobError(it.code))
            }
        }

    }
}