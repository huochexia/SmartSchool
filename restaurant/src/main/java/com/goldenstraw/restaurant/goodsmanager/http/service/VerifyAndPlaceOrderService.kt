package com.goldenstraw.restaurant.goodsmanager.http.service

import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Observable
import retrofit2.http.GET

interface VerifyAndPlaceOrderApi {

    /**
     * 获取订单
     */
    @GET("/1/classes/GoodsOfShoppingCart/")
    fun getAllOrder(date: String): Observable<MutableList<GoodsOfShoppingCart>>


}