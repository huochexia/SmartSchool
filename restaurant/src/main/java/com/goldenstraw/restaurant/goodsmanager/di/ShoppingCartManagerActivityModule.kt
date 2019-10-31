package com.goldenstraw.restaurant.goodsmanager.di

import com.goldenstraw.restaurant.goodsmanager.http.manager.shoppingcart.ShoppingServiceManagerImpl
import com.goldenstraw.restaurant.goodsmanager.http.service.ShoppingCartApi
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart.LocalShoppingCartDataSourceImpl
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart.RemoteShoppingCartDataSourceImpl
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart.ShoppingCartRepository
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.owner.basemodule.network.RetrofitFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

const val SHOPPING_CART_MANAGER_ACTIVITY_TAG = "SHOPPING_CART_MANAGER_ACTIVITY_TAG"

val shoppingcartdatasource = Kodein.Module(SHOPPING_CART_MANAGER_ACTIVITY_TAG) {

    bind<ShoppingCartRepository>() with singleton {
        ShoppingCartRepository(
            instance(),
            instance()
        )
    }

    bind<RemoteShoppingCartDataSourceImpl>() with singleton {
        RemoteShoppingCartDataSourceImpl(
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
    bind<ShoppingCartApi>() with singleton { instance<RetrofitFactory>().create(ShoppingCartApi::class.java) }

    bind<PrefsHelper>() with provider {
        PrefsHelper(instance())
    }
}