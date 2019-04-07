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
import android.content.ContextWrapper
import cn.bmob.v3.Bmob
import com.owner.basemodule.BuildConfig
import com.owner.basemodule.kodein.httpClientModule
import com.owner.basemodule.kodein.prefsModule
import com.owner.basemodule.logger.initLogger
import com.owner.basemodule.util.SingletonHolderNoArg
import com.squareup.leakcanary.LeakCanary
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

/**
 *
 * Created by Liuyong on 2019-03-26.It's smartschool
 *@description:
 */
class BaseApplication:Application(),KodeinAware {

    override val kodein: Kodein = Kodein.lazy {
        bind<Context>() with singleton { this@BaseApplication }
        import(androidModule(this@BaseApplication))
        import(androidXModule(this@BaseApplication))

        import(prefsModule)
    }

    override fun onCreate() {
        super.onCreate()

        //通过AppId连接Bmob云端
        Bmob.initialize(this, BMOB_APP_ID)

        initLogger(BuildConfig.DEBUG) //初始化Timber为DEBUG
        initLeakCanary()
    }

    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

   companion object :SingletonHolderNoArg<BaseApplication>(::BaseApplication){
       //Bmob库中smartSch库的Id
       const val BMOB_APP_ID :String= "5dd5e130b5927179da0304501d5914a5"
       const val BMOB_REST_API_KEY :String ="4dada1400b140cd29a9178cb0e89f36d"
       const val BASE_URL = "https://api2.bmob.cn"
   }
}

