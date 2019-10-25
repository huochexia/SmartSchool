package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectQuantity
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

/**
 * 网络访问Api
 */
interface QueryOrdersApi {
    /**
     * 从用户表中获取供应商信息
     */
    @GET("/1/users")
    fun getAllSupplier(@Query("where") condition: String): Observable<ObjectList<User>>

    /**
     * 按日期和供应商查询订单
     */
    @GET("/1/classes/OrderItem")
    fun getOrdersOfSupplier(@Query("where") condition: String): Observable<ObjectList<OrderItem>>
    /**
     * 修改订单,用于将发送错的订单还原为新订单，删除供应商名称
     */
    @PUT("/1/classes/OrderItem/{objectId}")
    fun updateOrderOfSupplier(@Body newOrder: ObjectSupplier, @Path("objectId") objectId: String): Completable

}