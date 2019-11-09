package com.goldenstraw.restaurant.goodsmanager.ui.recheck.util

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

interface RecheckOrderApi {
    /**
     * 获取订单,查询某个日期的订单
     */
    @GET("/1/classes/OrderItem/")
    fun getAllOrderOfDate(
        @Query("where") condition: String,
        @Query("order") name: String = "categoryCode",
        @Query("limit") limit: Int = 500
    ): Observable<ObjectList<OrderItem>>

    /**
     * 从用户表中获取供应商信息
     */
    @GET("/1/users")
    fun getAllSupplier(@Query("where") condition: String): Observable<ObjectList<User>>

    /**
     * 修改订单
     */
    @PUT("/1/classes/OrderItem/{objectId}")
    fun RecheckOrderItem(@Body newOrder: ObjectReCheck, @Path("objectId") objectId: String): Completable


    /**
     * 批量修改供应商实际数量
     */
    @POST("/1/batch/")
    fun batchReCheckQuantityOfOrder(@Body orders: BatchOrdersRequest<BatchRecheckObject>): Completable

    /**
     * 求各个供应商和
     */
    @GET("/1/classes/OrderItem")
    fun getTotalOfSuppliers(
        @Query("sum") sum: String = "total,againTotal",
        @Query("where") condition: String,
        @Query("groupby") group:String = "supplier"
    ): Observable<ObjectList<SupplierOfTotal>>
}