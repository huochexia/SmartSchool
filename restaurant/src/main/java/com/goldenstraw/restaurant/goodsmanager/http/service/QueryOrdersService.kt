package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User
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
     * 使用协程获取某个商品订单
     */
    @GET("/1/classes/OrderItem")
    suspend fun getOrdersList(
        @Query("where") where: String,
        @Query("limit") limit: Int = 400,
        @Query("skip") skip: Int = 0,
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
    suspend fun updateOrderOfSupplier(
        @Body newOrder: ObjectSupplier,
        @Path("objectId") objectId: String
    ): UpdateObject

    //得到某个类别的所有商品
    //where = {"categoryCode":"  "}
    @GET("/1/classes/Goods")
    suspend fun getGoodsOfCategory(
        @Query("where") condition: String,
        @Query("limit") limit: Int = 500,
        @Query("skip") skip: Int = 0
    )
            : ObjectList<Goods>

    //根据objectId获取单个商品信息
    @GET("/1/classes/Goods/{objectId}")
    fun getGoodsFromObjectId(@Path("objectId") id: String): Observable<Goods>

    /**
     * 按商品名称分组求和
     */
    @GET("/1/classes/OrderItem")
    suspend fun getTotalGroupByName(

        @Query("sum") checkQuantity: String = "checkQuantity,total",
        @Query("where") condition: String,
        @Query("groupby") groupby: String = "goodsName"
    ): ObjectList<SumByGroup>

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
    suspend fun updateNewPriceOfGoods(
        @Body newPrice: NewPrice,
        @Path("objectId") code: String
    ): UpdateObject

    /**
     * 从每日菜单库中查找菜单当中的菜谱
     */
    @GET("/1/classes/DailyMeal")
    suspend fun geDailyMeals(
        @Query("where") condition: String,
        @Query("limit") limit: Int = 200,
        @Query("skip") skip: Int = 0
    ): ObjectList<DailyMeal>

    /**
     * 从菜谱中获取原材料
     */
    @GET("/1/classes/RemoteCookBook/{objectId}")
    suspend fun getCookBook(
        @Path("objectId") objectId: String
    ): RemoteCookBook

    /**
     * 通过材料获取对应的商品。
     */
    @GET("/1/classes/Goods/{objectId}")
    suspend fun getGoods(@Path("objectId") id: String): Goods
}