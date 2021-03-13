package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

interface VerifyAndPlaceOrderApi {

    /**
     * 获取订单,查询某个日期的订单
     */
    @GET("/1/classes/OrderItem/")
    suspend fun getOrdersOfDate(
        @Query("where") condition: String,
        @Query("order") name: String = "categoryCode",
        @Query("limit") limit: Int = 500
    ):ObjectList<OrderItem>

    /**
     * 批量更新供应商
     */
    @POST("/1/batch/")
    fun batchAddOrderToSupplier(@Body orders: BatchOrdersRequest<ObjectSupplier>): Completable

    /**
     * 从用户表中获取供应商信息
     */
    @GET("/1/users")
    suspend fun getSupplier(@Query("where") condition: String): ObjectList<User>

    /**
     * 修改订单
     */
    @PUT("/1/classes/OrderItem/{objectId}")
    suspend fun updateOrderItem(
        @Body newOrder: ObjectQuantity,
        @Path("objectId") objectId: String
    ): UpdateObject

    /**
     * 修改数量
     */
    @PUT("/1/classes/OrderItem/{objectId}")
    suspend fun setCheckQuantity(
        @Body newCheck: ObjectCheckGoods,
        @Path("objectId") objectId: String
    ): UpdateObject



    /**
     * 批量修改供应商状态，确认和记帐
     */
    @POST("/1/batch/")
    fun batchCommitState(@Body orders: BatchOrdersRequest<ObjectState>): Completable


    /**
     * 删除订单
     */
    @DELETE("/1/classes/OrderItem/{objectId}")
    suspend fun deleteOrderItem(@Path("objectId") objectId: String): DeleteObject

}