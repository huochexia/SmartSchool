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

import arrow.core.Either
import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.base.repository.BaseRepositoryRemote
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.usercenter.http.entities.RegisterUserResp
import com.owner.usercenter.http.manager.UserServiceManager
import io.reactivex.Flowable

/**
 *注册所需数据源
 * Created by Liuyong on 2019-04-07.It's smartschool
 *@description:
 */
/**
 * 定义远程数据源的注册逻辑方法
 */
interface IRegisterRemoteDataSource : IRemoteDataSource {

    fun register(
        username: String,
        mobilephone: String,
        role: String,
        district: Int,
        categoryCode: String
    ): Flowable<Either<Errors, RegisterUserResp>>
}

/**
 * 实现远程数据源接口
 */
class RegisterRemoteDataSourec(
    private val serviceManager: UserServiceManager
) : IRegisterRemoteDataSource {
    override fun register(
        username: String,
        mobilephone: String,
        role: String,
        district: Int,
        categoryCode: String
    ): Flowable<Either<Errors, RegisterUserResp>> {
        return serviceManager.registerManager(username, mobilephone,role,district,categoryCode)
    }
}

/**
 * 定义与Processor交互的Repository
 */
class RegisterDataSourceRepository(
    remoteDataSource: IRegisterRemoteDataSource
) : BaseRepositoryRemote<IRegisterRemoteDataSource>(remoteDataSource) {

    fun register(
        username: String,
        mobilephone: String,
        role: String,
        district: Int,
        categoryCode: String
    ): Flowable<Either<Errors, RegisterUserResp>> =
        remoteDataSource.register(username, mobilephone, role, district, categoryCode)

}