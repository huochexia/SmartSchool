package com.goldenstraw.restaurant.goodsmanager.di

import com.goldenstraw.restaurant.goodsmanager.http.manager.GoodsServiceManagerImpl
import com.goldenstraw.restaurant.goodsmanager.http.service.GoodsApi
import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.repositories.LocalGoodsDateSourceImpl
import com.goldenstraw.restaurant.goodsmanager.repositories.RemoteGoodsDataSourceImpl
import com.goldenstraw.restaurant.goodsmanager.viewmodel.OrderMgViewModel
import com.owner.basemodule.network.RetrofitFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

/**
 *
 * 数据源对象容器，包含本地数据源和远程数据源
 * Created by Administrator on 2019/9/11 0011
 *
 */

const val GOODS_MANAGER_ACTIVITY_TAG = "GOODS_MANAGER_ACTIVITY_TAG"


val goodsDataSourceModule = Kodein.Module(GOODS_MANAGER_ACTIVITY_TAG) {


    //绑定商品数据源，对商品的操作，都是从这个类开始。
    bind<GoodsRepository>() with singleton { GoodsRepository(instance(), instance()) }

    //绑定本地数据源实现类,它需要的参数（AppDatabase）是全局的，所以由上一级容器提供
    bind<LocalGoodsDateSourceImpl>() with singleton { LocalGoodsDateSourceImpl(instance()) }

    //绑定远程数据源实现类
    bind<RemoteGoodsDataSourceImpl>() with singleton { RemoteGoodsDataSourceImpl(instance()) }

    //绑定GoodsServiceManagerImpl实例，它需要的GoodsApi由其它容器提供
    bind<GoodsServiceManagerImpl>() with singleton { GoodsServiceManagerImpl(instance()) }

    //绑定GoodsApi
    bind<GoodsApi>() with singleton { instance<RetrofitFactory>().create(GoodsApi::class.java) }
}