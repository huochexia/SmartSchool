package com.owner.basemodule.kodein

import com.owner.basemodule.network.RetrofitFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

const val HTTP_FACTORY_MODULE = "HTTP_FACTORY_MODULE"

val httpFactoryModule = Kodein.Module(HTTP_FACTORY_MODULE) {

    /**
     * 因为是通过工厂模式来创建API管理类，所以这里注入工厂类
     */
    bind<RetrofitFactory>() with singleton { RetrofitFactory.getInstance() }
}