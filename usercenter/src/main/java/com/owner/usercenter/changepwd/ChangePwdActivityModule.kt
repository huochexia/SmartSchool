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
package com.owner.usercenter.changepwd

import androidx.appcompat.app.AppCompatActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.network.RetrofitFactory
import com.owner.usercenter.http.manager.UserServiceManager
import com.owner.usercenter.http.manager.UserServiceManagerImpl
import com.owner.usercenter.http.service.UserApi
import com.owner.usercenter.util.PrefsHelper
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.*

/**
 *
 * Created by Liuyong on 2019-04-15.It's smartschool
 *@description:
 */

const val RESET_ACTIVITY_TAG = "RESET_ACTIVITY_TAG"

val changePwdModule = Kodein.Module(RESET_ACTIVITY_TAG) {

    bind<ChangePwdViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        context.getViewModel { ChangePwdViewModel(instance()) }
    }

    bind<ChangePwdActionProcessHolder>() with singleton {
        ChangePwdActionProcessHolder(instance(),instance())
    }

    bind<ChangePwdDataSourceRepository>() with singleton {
        ChangePwdDataSourceRepository(instance())
    }
    bind<PrefsHelper>() with provider {
        PrefsHelper(instance())
    }
    bind<ChangePwdRemoteDataSource>() with singleton {
        ChangePwdRemoteDataSource(instance())
    }

    bind<UserServiceManager>() with singleton {
        UserServiceManagerImpl(instance())
    }

    bind<UserApi>() with singleton {
        RetrofitFactory.getInstance().create(UserApi::class.java)
    }
}