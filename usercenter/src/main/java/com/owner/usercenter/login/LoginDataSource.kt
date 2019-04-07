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
package com.owner.usercenter.login

import arrow.core.Either
import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.usercenter.http.entities.LoginReq
import com.owner.usercenter.http.entities.LoginResp
import com.owner.usercenter.http.manager.UserServiceManager
import com.owner.usercenter.util.PrefsHelper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * 登录界面的数据源，一个是远程，验证用户，返回用户信息；一个是本地，保存登录信息，自动登录。
 * 所以这里要定义两个数据源的接口方法
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */
/**
 * 定义本地数据源的逻辑方法:保存从远程得到的用户登录信息，从本地提取登录信息，判断是否自动登录。
 */
interface ILoginLocalDataSource : ILocalDataSource {

    fun savePrefsUser(username: String, password: String): Completable
    //获取用户登录信息，因为存在成功和失败（无信息）两种可能，所以返回类型为Either<Errors,LoginEntity>
    fun fetchPresUser(): Flowable<Either<Errors, LoginReq>>

    fun isAutoLogin(): Single<Boolean>
}

/**
 * 定义远程数据源的逻辑方法
 */
interface ILoginRemoteDataSource : IRemoteDataSource {
    //登录，因为存在成功和失败两种可能的结果，所以要返回Either<Errors,LoginUser>类型结果
    fun login(username: String, password: String): Flowable<Either<Errors, LoginResp>>
}

/**
 * 定义登录数据仓库。注意：这个仓库的两个实参是以Kodein方式注入的。
 */
class LoginDataSourceRepository(
    remoteDataSource: ILoginRemoteDataSource,
    localDataSource: ILoginLocalDataSource
) : BaseRepositoryBoth<ILoginRemoteDataSource, ILoginLocalDataSource>(remoteDataSource, localDataSource) {
    //虽然最后向下游传递的数据类型还是Either，但是如果成功了还需要多一步保存操作，所以这里进行了处理
    //如果有左值，则继续下传数据；如果是右值，则先保存信息，然后继续下传
    fun login(username: String, password: String): Flowable<Either<Errors, LoginResp>> =
        remoteDataSource.login(username, password)
            .flatMap {either->
                either.fold({
                    Flowable.just(either)
                },{
                    localDataSource.savePrefsUser(username, password)//保存信息
                        .andThen(Flowable.just(either)) //然后再返回信息再次发出
                })
            }

    /*
     *从本地共享文件中获取用户登录信息
     */
    fun prefsUser(): Flowable<Either<Errors, LoginReq>> =
        localDataSource.fetchPresUser()

    /*
     * 从本地共享文件中获取用户是否可以自动登录的信息
     */
    fun prefsAutoLogin(): Single<Boolean> =
        localDataSource.isAutoLogin()
}

/**
 * 实现本地数据源接口的实例,需要传入一个SharedPreferences代理工具，由它来完成本地共享文件的读写操作
 */
class LoginLocalDataSource(
    private val prefs: PrefsHelper
) : ILoginLocalDataSource {
    override fun savePrefsUser(username: String, password: String): Completable =
    //生成一个Completable数据流，它完成给prefs属性赋值的动作
        Completable.fromAction {
            prefs.username = username
            prefs.password = password
        }


    override fun fetchPresUser(): Flowable<Either<Errors, LoginReq>> =
    //生成一个prefs数据流，然后将它的内容转换成Either<Errors,LoginEntity>类数据
        Flowable.just(prefs)
            .map {
                when (it.username.isNotEmpty() && it.password.isNotEmpty()) {
                    true -> Either.right(LoginReq(it.username, it.password))
                    false -> Either.left(Errors.EmptyResultsError)
                }
            }


    override fun isAutoLogin(): Single<Boolean> =
    //生成一个Single数据流，它发射一个数据。
        Single.just(prefs.autoLogin)
}

/**
 * 从数据源的管理类中获取数据。
 */
class LoginRemoteDataSource(private val serviceManager: UserServiceManager) : ILoginRemoteDataSource {
    override fun login(username: String, password: String): Flowable<Either<Errors, LoginResp>> {
        return serviceManager.loginManager(username, password)
    }
}