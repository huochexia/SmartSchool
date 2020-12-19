package com.goldenstraw.restaurant.goodsmanager.di

import com.goldenstraw.restaurant.goodsmanager.http.manager.shoppingcar.ShoppingServiceManagerImpl
import com.goldenstraw.restaurant.goodsmanager.http.service.ShoppingCarApi
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcar.LocalShoppingCartDataSourceImpl
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcar.RemoteShoppingCarDataSourceImpl
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcar.ShoppingCarRepository
import com.owner.basemodule.network.RetrofitFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

const val SHOPPING_CART_MANAGER_ACTIVITY_TAG = "SHOPPING_CART_MANAGER_ACTIVITY_TAG"

val shoppingcartdatasource = Kodein.Module(SHOPPING_CART_MANAGER_ACTIVITY_TAG) {

    bind<ShoppingCarRepository>() with singleton {
        ShoppingCarRepository(
            instance(),
            instance()
        )
    }

    bind<RemoteShoppingCarDataSourceImpl>() with singleton {
        RemoteShoppingCarDataSourceImpl(
            instance()
        )
    }
    bind<LocalShoppingCartDataSourceImpl>() with singleton {
        LocalShoppingCartDataSourceImpl(
            instance()
        )
    }

    bind<ShoppingServiceManagerImpl>() with singleton {
        ShoppingServiceManagerImpl(
            instance()
        )
    }
    bind<ShoppingCarApi>() with singleton { instance<RetrofitFactory>().create(ShoppingCarApi::class.java) }

//    bind<PrefsHelper>() with provider {
//        PrefsHelper(instance())
//    }
}