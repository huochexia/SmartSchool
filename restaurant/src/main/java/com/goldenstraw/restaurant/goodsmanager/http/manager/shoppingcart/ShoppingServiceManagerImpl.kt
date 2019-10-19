package com.goldenstraw.restaurant.goodsmanager.http.manager.shoppingcart

import com.goldenstraw.restaurant.goodsmanager.http.entities.NewOrderItem
import com.goldenstraw.restaurant.goodsmanager.http.manager.shoppingcart.IShoppingCartServiceManager
import com.goldenstraw.restaurant.goodsmanager.http.service.ShoppingCartApi
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable

class ShoppingServiceManagerImpl(
    private val serviceApi: ShoppingCartApi
) : IShoppingCartServiceManager {

    override fun addShoppingCart(orderItem: NewOrderItem): Completable {
        return serviceApi.createShoppingCart(orderItem)
    }
}