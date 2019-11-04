package com.goldenstraw.restaurant.goodsmanager.ui.recheck.util

import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.LocalPlaceOrderDataSourceImpl
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.owner.basemodule.network.RetrofitFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

const val RECHECK_ORDER_ACTIVITY_MODULE = "RECHECK_ORDER_ACTIVITY_MODULE"

val recheckorderactivitymodule = Kodein.Module(RECHECK_ORDER_ACTIVITY_MODULE) {

    bind<RecheckOrderRepository>() with singleton {
        RecheckOrderRepository(instance())
    }

    bind<RecheckOrderDataSourceImpl>() with singleton {
        RecheckOrderDataSourceImpl(instance())
    }

    bind<RecheckOrderManageImpl>() with singleton {
        RecheckOrderManageImpl(instance())
    }
    bind<RecheckOrderApi>() with singleton {
        instance<RetrofitFactory>().create(RecheckOrderApi::class.java)
    }

    bind<PrefsHelper>() with provider {
        PrefsHelper(
            instance()
        )
    }
}