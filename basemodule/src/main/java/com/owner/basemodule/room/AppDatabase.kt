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
package com.owner.basemodule.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.owner.basemodule.base.ROOM_DATABASE_FILE
import com.owner.basemodule.room.dao.GoodsManagerDao
import com.owner.basemodule.room.dao.UserDao
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.User

/**
 *
 * Created by Liuyong on 2019-04-30.It's smartschool
 *@description:
 */
@Database(version = 1, entities = [User::class, Goods::class, GoodsCategory::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao //用户管理
    abstract fun goodsDao(): GoodsManagerDao //订单管理

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, ROOM_DATABASE_FILE)
                .build()
        }
    }
}