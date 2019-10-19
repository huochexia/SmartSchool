package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entities.NewOrderItem
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.POST

interface ShoppingCartApi {
    //1、将购物车商品增加到远程
    @POST("/1/classes/OrderItem")
    fun createShoppingCart(@Body orderItem: NewOrderItem): Completable
}