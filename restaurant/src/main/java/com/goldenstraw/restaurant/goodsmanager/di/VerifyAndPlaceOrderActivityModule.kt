package com.goldenstraw.restaurant.goodsmanager.di

import com.goldenstraw.restaurant.goodsmanager.http.manager.place_order.VerifyAndPlaceOrderManageImpl
import com.goldenstraw.restaurant.goodsmanager.http.service.VerifyAndPlaceOrderApi
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.RemotePlaceOrderDataSourceImpl
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.owner.basemodule.network.RetrofitFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

const val VERIFY_AND_PLACE_ORDER_ACTIVITY_MODULE = "VERIFY_AND_PLACE_ORDER_ACTIVITY_MODULE"

val verifyandplaceorderdatasource = Kodein.Module(VERIFY_AND_PLACE_ORDER_ACTIVITY_MODULE) {

    bind<VerifyAndPlaceOrderRepository>() with singleton {
        VerifyAndPlaceOrderRepository(instance())
    }

    bind<RemotePlaceOrderDataSourceImpl>() with singleton {
        RemotePlaceOrderDataSourceImpl(instance())
    }

    bind<VerifyAndPlaceOrderManageImpl>() with singleton {
        VerifyAndPlaceOrderManageImpl(instance())
    }
    bind<VerifyAndPlaceOrderApi>() with singleton {
        instance<RetrofitFactory>().create(VerifyAndPlaceOrderApi::class.java)
    }
}