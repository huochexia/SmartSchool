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
package com.owner.basemodule.base

import android.app.Application
import android.content.Context
import cn.bmob.v3.Bmob
import com.owner.basemodule.BuildConfig
import com.owner.basemodule.kodein.*
import com.owner.basemodule.logger.initLogger
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.util.SingletonHolderNoArg
import com.squareup.leakcanary.LeakCanary
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

/**
 * 基础应用，实现KodeinAware接口
 * Created by Liuyong on 2019-03-26.It's smartschool
 *@description:
 */
class BaseApplication:Application(),KodeinAware {

    //依赖容器
    override val kodein: Kodein = Kodein.lazy {

        bind<Context>() with singleton { this@BaseApplication }

//        bind<AppDatabase>() with singleton {
//            AppDatabase.getInstance(instance())
//        }

//        import(androidModule(this@BaseApplication))
//        import(androidXModule(this@BaseApplication))

        import(prefsModule)
        import(httpFactoryModule)  //注入全局Retrofit工厂
        import(roomDBModule) //注入全局Room数据库对象
    }

    override fun onCreate() {
        super.onCreate()

        //通过AppId连接Bmob云端
        Bmob.initialize(this, BMOB_APP_ID)
        //初始化本地数据库

        initLogger(BuildConfig.DEBUG) //初始化Timber为DEBUG
        initLeakCanary()
    }

    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    companion object : SingletonHolderNoArg<BaseApplication>(::BaseApplication)


}

