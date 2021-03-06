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
import com.owner.basemodule.room.dao.*
import com.owner.basemodule.room.entities.*

/**
 *
 * Created by Liuyong on 2019-04-30.It's smartschool
 *@description:
 */
@Database(
    version = 1,
    entities = [User::class,
        Goods::class,
        GoodsCategory::class,
        LocalCookBook::class,
        FoodOfShoppingCar::class,
        MaterialOfShoppingCar::class,
        NewOrder::class,
        Material::class
    ],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao //用户管理
    abstract fun goodsDao(): GoodsDao //商品管理
    abstract fun cookbookDao(): CookBookDao //菜谱管理
    abstract fun shoppingCarDao():ShoppingCarDao //购物车管理
    abstract fun orderDao():OrderDao //订单管理

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
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()//升级数据库时如果出现异常则删除数据库重新创建。
                .build()
        }
    }
}