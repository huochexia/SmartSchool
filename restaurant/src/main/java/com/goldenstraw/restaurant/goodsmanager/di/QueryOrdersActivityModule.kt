package com.goldenstraw.restaurant.goodsmanager.di

import com.goldenstraw.restaurant.goodsmanager.http.manager.query_orders.QueryOrdersManagerImpl
import com.goldenstraw.restaurant.goodsmanager.http.service.QueryOrdersApi
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.RemoteQueryOrdersDataSourceImpl
import com.owner.basemodule.network.RetrofitFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

const val QUERY_ORDERS_ACTIVITY_MODULE = "QUERY_ORDERS_ACTIVITY_MODULE"

val queryordersactivitymodule = Kodein.Module(QUERY_ORDERS_ACTIVITY_MODULE) {

    bind<QueryOrdersRepository>() with singleton {
        QueryOrdersRepository(instance())
    }
    bind<RemoteQueryOrdersDataSourceImpl>() with singleton {
        RemoteQueryOrdersDataSourceImpl(instance())
    }
    bind<QueryOrdersManagerImpl>() with singleton {
        QueryOrdersManagerImpl(instance())
    }
    bind<QueryOrdersApi>() with singleton {
        instance<RetrofitFactory>().create(QueryOrdersApi::class.java)
    }
}