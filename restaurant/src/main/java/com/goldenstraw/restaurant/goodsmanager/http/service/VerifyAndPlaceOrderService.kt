package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

interface VerifyAndPlaceOrderApi {

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
     * 批量更新供应商
     */
    @POST("/1/batch/")
    fun batchAddOrderToSupplier(@Body orders: BatchOrdersRequest<ObjectSupplier>): Completable

    /**
     * 从用户表中获取供应商信息
     */
    @GET("/1/users")
    fun getAllSupplier(@Query("where") condition: String): Observable<ObjectList<User>>

    /**
     * 修改订单
     */
    @PUT("/1/classes/OrderItem/{objectId}")
    fun updateOrderItem(@Body newOrder: ObjectQuantity, @Path("objectId") objectId: String): Completable

    /**
     * 修改数量
     */
    @PUT("/1/classes/OrderItem/{objectId}")
    fun setCheckQuantity(@Body newCheck: ObjectCheckGoods, @Path("objectId") objectId: String): Completable



    /**
     * 批量修改供应商状态，确认和记帐
     */
    @POST("/1/batch/")
    fun batchCommitState(@Body orders: BatchOrdersRequest<ObjectState>): Completable



    /**
     * 删除订单
     */
    @DELETE("/1/classes/OrderItem/{objectId}")
    fun deleteOrderItem(@Path("objectId") objectId: String): Completable

}