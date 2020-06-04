package com.goldenstraw.restaurant.goodsmanager.di


import com.goldenstraw.restaurant.goodsmanager.http.manager.cookbok.CookBookServiceManagerImpl
import com.goldenstraw.restaurant.goodsmanager.http.service.CookBookApi
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.LocalCookBookDataSourceImpl
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.RemoteCookBookDataSourceImpl
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.owner.basemodule.network.RetrofitFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

/**
 * 创建依赖注入检索池,从数据管理库开始，一直到获取网络API
 */
const val COOK_BOOK_ACTIVITY_MODULE = "COOK_BOOK_ACTIVITY_MODULE"

val cookbookactivitymodule = Kodein.Module(COOK_BOOK_ACTIVITY_MODULE) {

    /*
     *1、数据管理库
     */
    bind<CookBookRepository>() with singleton {
        CookBookRepository(instance(), instance())
    }
    /*
      2、为数据管理库提供远程数据源
     */
    bind<RemoteCookBookDataSourceImpl>() with singleton {
        RemoteCookBookDataSourceImpl(instance())
    }
    bind<LocalCookBookDataSourceImpl>() with singleton {
        LocalCookBookDataSourceImpl(instance())
    }
    /*
    3、为远程数据源提供数据管理者
     */
    bind<CookBookServiceManagerImpl>() with singleton {
        CookBookServiceManagerImpl(instance())
    }

    /*
    4、为数据管理者提供API
     */
    bind<CookBookApi>() with singleton {
        instance<RetrofitFactory>().create(CookBookApi::class.java)
    }

    bind<PrefsHelper>() with provider {
        PrefsHelper(instance())
    }
}