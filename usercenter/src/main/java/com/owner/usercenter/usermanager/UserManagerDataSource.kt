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
package com.owner.usercenter.usermanager

import arrow.core.Either
import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.entities.User
import com.owner.usercenter.http.manager.UserServiceManager
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 *管理本地数据缓存
 * Created by Liuyong on 2019-05-01.It's smartschool
 *@description:
 */

interface ILocalUserManagerDataSource : ILocalDataSource {
    //得到用户列表
    fun getAllUsersFromLocal(): Observable<Either<Errors, List<User>>>

    //插入所有列表
    fun saveAllUsers(either: Either<Errors, List<User>>): Completable

    //按用户名查询用户
    fun search(name: String): Observable<List<User>>
}

class LocalUserManagerDataSourceImpl(
    private val database: AppDatabase
) : ILocalUserManagerDataSource {

    override fun saveAllUsers(either: Either<Errors, List<User>>): Completable {

        return either.fold({
            Completable.complete()
        }, {
            database.userDao().insertUsers(it)
                .andThen(Completable.complete())
        })
    }

    override fun getAllUsersFromLocal(): Observable<Either<Errors, List<User>>> {
        return database.userDao().getAllUsers()
            .subscribeOn(Schedulers.io())
            .map {
                when (it.isEmpty()) {
                    true -> Either.left(Errors.EmptyResultsError)
                    false -> Either.right(it)
                }
            }
    }

    override fun search(name: String): Observable<List<User>> {
        return database.userDao().search(name)
    }
}

/**
 * 管理远程数据库数据
 */
interface IRemoteUserManagerDataSource : IRemoteDataSource {

    //从远程得到用户列表
    fun getAllUserFromRemote(): Flowable<Either<Errors, List<User>>>

    suspend fun deleteUser(token: String, objectId: String): DeleteObject
}

class RemoteUserManagerDataSourceImpl(
    private val service: UserServiceManager
) : IRemoteUserManagerDataSource {

    override fun getAllUserFromRemote(): Flowable<Either<Errors, List<User>>> =
        service.getAllUsers()
            .subscribeOn(Schedulers.io())
            .map {
                when (it.results.isEmpty()) {
                    true -> Either.left(Errors.EmptyResultsError)
                    false -> Either.right(it.results)
                }
            }

    override suspend fun deleteUser(token: String, objectId: String): DeleteObject {
        return service.deleteUser(token, objectId)
    }
}

/**
 * 用户管理数据仓库，合并本地与远程
 */
class UserManagerRepository(
    remoteDataSource: IRemoteUserManagerDataSource,
    localDataSource: LocalUserManagerDataSourceImpl
) : BaseRepositoryBoth<IRemoteUserManagerDataSource, ILocalUserManagerDataSource>(remoteDataSource, localDataSource) {

    //从远程得到用户列表，并保存在本地数据缓存文件中
    fun getAllUsersList(): Flowable<Either<Errors, List<User>>> {
        return remoteDataSource.getAllUserFromRemote().flatMap { either ->
            localDataSource.saveAllUsers(either).andThen(Flowable.just(either))
        }
    }

    //从本地列表中获得查询结果

    fun getSearchUserList(username: String): Observable<List<User>> {

        return localDataSource.search(username)

    }

    //删除用户
    suspend fun deleteUser(token: String, objectId: String): DeleteObject {
        return remoteDataSource.deleteUser(token, objectId)
    }
}


