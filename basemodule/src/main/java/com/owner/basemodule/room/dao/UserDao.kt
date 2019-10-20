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
package com.owner.basemodule.room.dao

import androidx.room.*
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

/**
 *
 * Created by Liuyong on 2019-04-30.It's smartschool
 *@description:
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    fun getAllUsers(): Observable<MutableList<User>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(users: List<User>): Completable

    @Update
    fun update(user: User): Completable

    @Query("SELECT * FROM user WHERE objectId = :userId")
    fun getUser(userId: String): Observable<User>

    //获取某个角色的用户
    @Query("SELECT * FROM user WHERE role = :rolename")
    fun getUserOfRole(rolename: String): Observable<MutableList<User>>
    //因为是可观察的查询，所以发生其他更改，如插入，删除时会发射通知，所以避免由于用户更新时发射通知
//    fun getDistinctUser(userId: String): Flowable<User> = getUser(userId).distinctUntilChanged()

    @Query("SELECT * From user WHERE username LIKE '%' || :name || '%'")
    fun search(name: String): Observable<List<User>>

    @Query("DELETE FROM User")
    fun clearUser(): Completable
}