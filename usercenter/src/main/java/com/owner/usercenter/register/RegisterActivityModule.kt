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

import androidx.appcompat.app.AppCompatActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.network.RetrofitFactory
import com.owner.usercenter.http.manager.UserServiceManager
import com.owner.usercenter.http.manager.UserServiceManagerImpl
import com.owner.usercenter.http.service.UserApi
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

/**
 *
 * Created by Liuyong on 2019-04-09.It's smartschool
 *@description:
 */
const val REGISTER_KODEIN_TAG = "REGISTER_KODEIN_TAG"

val registerKodeinModule = Kodein.Module(REGISTER_KODEIN_TAG) {

    bind<RegisterViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        context.getViewModel { RegisterViewModel(instance()) }
    }

    bind<RegisterActionProcessHolder>() with singleton {
        RegisterActionProcessHolder(instance())
    }

    bind<RegisterDataSourceRepository>() with singleton {
        RegisterDataSourceRepository(instance())
    }

    bind<RegisterRemoteDataSourec>() with singleton {
        RegisterRemoteDataSourec(instance())
    }

    bind<UserServiceManager>() with singleton {
        UserServiceManagerImpl(instance())
    }

    bind<UserApi>() with singleton {
        RetrofitFactory.getInstance().create(UserApi::class.java)
    }
}