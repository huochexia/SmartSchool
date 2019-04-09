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

import androidx.appcompat.app.AppCompatActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.network.RetrofitFactory
import com.owner.usercenter.http.manager.UserServiceManager
import com.owner.usercenter.http.manager.UserServiceManagerImpl
import com.owner.usercenter.http.service.UserApi
import com.owner.usercenter.http.service.UserService
import com.owner.usercenter.util.PrefsHelper
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.*

/**
 *依赖注入，注入登录所需要的实例类
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */

const val LOGIN_MODULE_TAG = "LOGIN_MODULE_TAG"

val loginKodeinModule = Kodein.Module(LOGIN_MODULE_TAG) {

    bind<LoginViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        context.getViewModel { LoginViewModel(instance()) }
    }
    //给ViewModel提供类
    bind<LoginActionProcessHolder>() with singleton {
        LoginActionProcessHolder(instance())
    }
    //给LoginActionProcessHolder提供类
    bind<LoginDataSourceRepository>() with singleton {
        LoginDataSourceRepository(
            remoteDataSource = instance(),
            localDataSource = instance()
        )
    }
    //给LoginDataSourceRepository提供类, 它需要的类由基础Module提供。
    bind<LoginLocalDataSource>() with singleton {
        LoginLocalDataSource(prefs = instance())
    }
    //给它提供实例的Module在应用程序的Kodein容器中
    bind<PrefsHelper>() with provider {
        PrefsHelper(instance())
    }

    //给LoginDataSourceRepository提供类
    bind<LoginRemoteDataSource>() with singleton {
        LoginRemoteDataSource(instance())
    }
    //给LoginRemoteDataSource提供类
    bind<UserServiceManager>() with singleton {
        UserServiceManagerImpl(instance())
    }
    bind<UserApi>() with singleton {
        RetrofitFactory.getInstance().create(UserApi::class.java)
    }
}