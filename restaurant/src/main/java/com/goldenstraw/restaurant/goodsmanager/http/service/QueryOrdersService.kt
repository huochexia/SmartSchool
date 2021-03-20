package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.Goods
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
   suspend fun getAllSupplier(@Query("where") condition: String): ObjectList<User>

    /**
     * 按日期和供应商查询订单
     */
    @GET("/1/classes/OrderItem")
    fun getAllofOrders(
        @Query("where") condition: String,
        @Query("limit") limit: Int = 500
    ): Observable<ObjectList<OrderItem>>

    /**
     * 使用协程获取某个商品订单
     */
    @GET("/1/classes/OrderItem")
    suspend fun getOrdersList(
        @Query("where") where: String,
        @Query("order") order: String = "-createdAt"
    ): ObjectList<OrderItem>

    /**
     * 修改订单的单价信息
     */
    @PUT("/1/classes/OrderItem/{ObjectId}")
    suspend fun updateUnitPriceOfOrders(
        @Body newPriceOfOrders: ObjectUnitPrice,
        @Path("ObjectId") objectId: String
    ): UpdateObject

    /**
     * 删除尚未做任何处理的订单，主要是厨师提交订单后的删除
     */
    @DELETE("/1/classes/OrderItem/{ObjectId}")
    suspend fun deleteOrderItem(@Path("ObjectId") objectId: String):DeleteObject

    /**
     * 修改订单信息，如数量和备注
     */
    @PUT("/1/classes/OrderItem/{ObjectId}")
    suspend fun updateOrderItem(
        @Body newOrderItem: ObjectQuantityAndNote,
        @Path("ObjectId") objectId: String
    ):UpdateObject

    /**
     * 修改订单,用于将发送错的订单还原为新订单，删除供应商名称
     */
    @PUT("/1/classes/OrderItem/{objectId}")
    fun updateOrderOfSupplier(
        @Body newOrder: ObjectSupplier,
        @Path("objectId") objectId: String
    ): Completable

    //得到某个类别的所有商品
    //where = {"categoryCode":"  "}
    @GET("/1/classes/Goods")
    fun getGoodsOfCategory(@Query("where") condition: String, @Query("limit") limit: Int = 500)
            : ObjectList<Goods>

    //根据objectId获取单个商品信息
    @GET("/1/classes/Goods/{objectId}")
    fun getGoodsFromObjectId(@Path("objectId") id: String): Observable<Goods>

    /**
     * 按商品名称分组求和
     */
    @GET("/1/classes/OrderItem")
    fun getTotalGroupByName(

        @Query("sum") checkQuantity: String = "checkQuantity,total",
        @Query("where") condition: String,
        @Query("groupby") groupby: String = "goodsName"
    ): Observable<ObjectList<SumByGroup>>

    /**
     * 求和
     */
    @GET("/1/classes/OrderItem")
   suspend fun getTotalOfSupplier(
        @Query("sum") total: String = "total",
        @Query("where") condition: String
    ): ObjectList<SumResult>

    /**
    UPDATE
     */
    //提交新单价
    @PUT("/1/classes/Goods/{objectId}")
    fun updateNewPriceOfGoods(@Body newPrice: NewPrice, @Path("objectId") code: String): UpdateObject

    /**
     * 从每日菜单库中查找菜单当中的菜谱
     */
    @GET("/1/classes/DailyMeal")
     fun getCookBookOfDailyMeal(
        @Query("where") condition: String,
        @Query("limit") limit: Int = 500

    ): Observable<ObjectList<DailyMeal>>

}