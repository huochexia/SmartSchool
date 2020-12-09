package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.CBGCrossRef
import com.owner.basemodule.room.entities.CookBooks
import retrofit2.http.*

/**
 * 菜谱这部分数据访问使用协程
 *
 */
interface CookBookApi {
    /*
     * 生成
     */
    //增加菜谱
    @POST("/1/classes/CookBooks")
    suspend fun createCookBook(@Body newCookBook: NewCookBook): CreateObject

    //增加菜谱与商品关系
    @POST("/1/classes/CBGCrossRef")
    suspend fun createCrossRef(@Body newRef: NewCrossRef): CreateObject

    //增加每日菜单
    @POST("/1/classes/DailyMeal")
    suspend fun createDailyMeal(@Body dailyMeal: NewDailyMeal): CreateObject

    /*
     删除
     */
    //删除菜谱
    @DELETE("/1/classes/CookBooks/{objectId}")
    suspend fun deleteCookBook(@Path("objectId") objectId: String): DeleteObject

    //删除关系
    @DELETE("/1/classes/CBGCrossRef/{objectId}")
    suspend fun deleteCrossRef(@Path("objectId") objectId: String): DeleteObject

    //删除每日菜单
    @DELETE("/1/classes/DailyMeal/{objectId}")
    suspend fun deleteDailyMeal(@Path("objectId") objectId: String): DeleteObject

    /*
      修改
     */
    //修改菜谱
    @PUT("/1/classes/CookBooks/{objectId}")
    suspend fun updateCookBook(
        @Body newCookBook: NewCookBook,
        @Path("objectId") objectId: String
    ): UpdateObject

    //修改每日菜单
    @PUT("/1/classes/DailyMeal/{objectId}")
    suspend fun updateDailyMeal(
        @Body newDailyMeal: UpdateIsteacher,
        @Path("objectId") objectId: String
    ): UpdateObject

    //修改菜谱状态
    @PUT("/1/classes/CookBooks/{objectId}")
    suspend fun updateCookBookState(
        @Body newCookBook: UpdateIsStandby,
        @Path("objectId") objectId: String
    ): UpdateObject

    //修改菜谱使用次数
    @PUT("/1/classes/CookBooks/{objectId}")
    suspend fun updateNumberOfUsed(
        @Body newCookBook: UpdateUsedNumber,
        @Path("objectId") objectId: String
    ): UpdateObject

    /*
      查询
     */

    //查询某日的菜单,使用协程
    @GET("/1/classes/DailyMeal/")
    suspend fun getDailyMealOfDate(@Query("where") where: String): ObjectList<DailyMeal>

    //查询某类菜谱
    @GET("/1/classes/CookBooks/")
    suspend fun getCookBookOfCategory(
        @Query("where") where: String,
        @Query("skip") skip: Int,
        @Query("limit") limit: Int = 500
    ): ObjectList<CookBooks>

    @GET("/1/classes/CookBooks/{objectId}")
    suspend fun getCookBook(@Path("objectId") objectId: String):CookBooks

    //获取某个菜谱与商品的关联关系
    @GET("/1/classes/CBGCrossRef/")
    suspend fun getCookBookGoodsCrossRef(
        @Query("where") where: String,
        @Query("skip") skip: Int,
        @Query("limit") limit: Int = 500
    ): ObjectList<CBGCrossRef>


}
