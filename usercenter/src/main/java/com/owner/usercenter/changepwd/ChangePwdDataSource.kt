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
package com.owner.usercenter.changepwd

import arrow.core.Either
import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.base.repository.BaseRepositoryRemote
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.usercenter.http.entities.ChangePwdResp
import com.owner.usercenter.http.manager.UserServiceManager
import io.reactivex.Flowable

/**
 *
 * Created by Liuyong on 2019-04-12.It's smartschool
 *@description:
 */
interface IChangePwdRemoteDataSource : IRemoteDataSource {
    fun resetPwd(
        sessionToken: String,
        objectId: String,
        oldPassword: String, newPassword: String
    ): Flowable<Either<Errors, ChangePwdResp>>
}

class ChangePwdRemoteDataSource(
    private val serviceManager: UserServiceManager
) : IChangePwdRemoteDataSource {
    override fun resetPwd(
        sessionToken: String,
        objectId: String,
        oldPassword: String,
        newPassword: String
    ): Flowable<Either<Errors, ChangePwdResp>> =
        serviceManager.changePwd(sessionToken, objectId, oldPassword, newPassword)


}


class ChangePwdDataSourceRepository(
    remoteDataSource: IChangePwdRemoteDataSource
) : BaseRepositoryRemote<IChangePwdRemoteDataSource>(remoteDataSource) {

    fun resetPwd(
        sessionToken: String,
        objectId: String,
        oldPassword: String,
        newPassword: String
    ): Flowable<Either<Errors, ChangePwdResp>> {
        return remoteDataSource.resetPwd(sessionToken, objectId, oldPassword, newPassword)
    }
}