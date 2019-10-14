package com.goldenstraw.restaurant.goodsmanager.http.manager

import com.goldenstraw.restaurant.goodsmanager.http.service.ShoppingCartApi
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable

class ShoppingServiceManagerImpl(
    private val serviceApi: ShoppingCartApi
) : IShoppingCartServiceManager {

    override fun addShoppingCart(shoppingCart: GoodsOfShoppingCart): Completable {
        return serviceApi.createShoppingCart(shoppingCart)
    }
}