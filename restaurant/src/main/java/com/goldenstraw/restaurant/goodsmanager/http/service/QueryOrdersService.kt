package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.ObjectList
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
    fun getAllSupplier(@Query("where") condition: String): Observable<ObjectList<User>>

    /**
     * 按日期和供应商查询订单
     */
    @GET("/1/classes/OrderItem")
    fun getAllofOrders(
        @Query("where") condition: String,
        @Query("limit") limit: Int = 500
    ): Observable<ObjectList<OrderItem>>

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
            : Observable<ObjectList<Goods>>

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
    fun getTotalOfSupplier(
        @Query("sum") total: String = "total",
        @Query("where") condition: String
    ): Observable<ObjectList<SumResult>>

    /**
    UPDATE
     */
    //提交新单价
    @PUT("/1/classes/Goods/{objectId}")
    fun updateNewPriceOfGoods(@Body newPrice: NewPrice, @Path("objectId") code: String): Completable

    /**
     * 从每日菜单库中查找菜单当中的菜谱
     */
    @GET("/1/classes/DailyMeal")
     fun getCookBookOfDailyMeal(
        @Query("where") condition: String,
        @Query("limit") limit: Int = 500

    ): Observable<ObjectList<DailyMeal>>

}