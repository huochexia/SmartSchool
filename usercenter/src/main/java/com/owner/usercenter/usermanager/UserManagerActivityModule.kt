package com.owner.usercenter.usermanager

import androidx.appcompat.app.AppCompatActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.network.RetrofitFactory
import com.owner.usercenter.http.manager.UserServiceManagerImpl
import com.owner.usercenter.http.service.UserApi
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

const val USER_MANAGER_ACTIVITY_MODULE = "USER_MANAGER_ACTIVITY_MODULE"

val usermanageractivitymodule = Kodein.Module(USER_MANAGER_ACTIVITY_MODULE) {


    bind<UserManagerRepository>() with singleton {
        UserManagerRepository(
            instance(),
            instance()
        )
    }
    bind<LocalUserManagerDataSourceImpl>() with singleton {
        LocalUserManagerDataSourceImpl(instance())
    }
    bind<RemoteUserManagerDataSourceImpl>() with singleton {
        RemoteUserManagerDataSourceImpl(instance())
    }
    bind<UserServiceManagerImpl>() with singleton {
        UserServiceManagerImpl(instance())
    }
    bind<UserApi>() with singleton {
        instance<RetrofitFactory>().create(UserApi::class.java)
    }
}