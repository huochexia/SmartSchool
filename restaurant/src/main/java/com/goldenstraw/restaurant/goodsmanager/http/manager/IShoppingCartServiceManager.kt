package com.goldenstraw.restaurant.goodsmanager.http.manager

import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable

interface IShoppingCartServiceManager {
    //1、增加购物车
    fun addShoppingCart(shoppingCart: GoodsOfShoppingCart): Completable
}