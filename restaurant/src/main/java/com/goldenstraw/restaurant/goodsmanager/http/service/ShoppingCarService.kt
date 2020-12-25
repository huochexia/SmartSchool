package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.owner.basemodule.room.entities.NewOrder
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.POST

interface ShoppingCarApi {

    //1、将购物车商品增加到远程
    @POST("/1/batch/")
    fun batchCreateNewOrderItems(@Body newOrderItem: BatchOrdersRequest<NewOrder>): Completable

}