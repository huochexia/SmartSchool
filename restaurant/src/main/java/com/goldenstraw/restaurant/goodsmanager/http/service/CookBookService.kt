package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import retrofit2.http.*

/**
 * 菜谱这部分数据访问使用协程
 *
 */
interface CookBookApi {
    /*
     * 生成
     */

    //增加每日菜单
    @POST("/1/classes/DailyMeal")
    suspend fun createDailyMeal(@Body dailyMeal: NewDailyMeal): CreateObject

    /*
     删除
     */
    //删除菜谱
    @DELETE("/1/classes/RemoteCookBook/{objectId}")
    suspend fun deleteCookBook(@Path("objectId") objectId: String): DeleteObject


    //删除每日菜单
    @DELETE("/1/classes/DailyMeal/{objectId}")
    suspend fun deleteDailyMeal(@Path("objectId") objectId: String): DeleteObject

    /*
      修改
     */
    //修改菜谱
    @PUT("/1/classes/RemoteCookBook/{objectId}")
    suspend fun updateCookBook(
        @Body newCookBook: RemoteCookBook,
        @Path("objectId") objectId: String
    ): UpdateObject

    //修改每日菜单
    @PUT("/1/classes/DailyMeal/{objectId}")
    suspend fun updateDailyMeal(
        @Body newDailyMeal: UpdateIsteacher,
        @Path("objectId") objectId: String
    ): UpdateObject

    //修改菜谱状态
    @PUT("/1/classes/RemoteCookBook/{objectId}")
    suspend fun updateCookBookState(
        @Body newCookBook: UpdateIsStandby,
        @Path("objectId") objectId: String
    ): UpdateObject

    //修改菜谱使用次数
    @PUT("/1/classes/RemoteCookBook/{objectId}")
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
    @GET("/1/classes/RemoteCookBook/")
    suspend fun getCookBookOfCategory(
        @Query("where") where: String,
        @Query("skip") skip: Int,
        @Query("limit") limit: Int = 500
    ): ObjectList<RemoteCookBook>

    @GET("/1/classes/RemoteCookBook/{objectId}")
    suspend fun getCookBook(@Path("objectId") objectId: String): RemoteCookBook

    //查询数量结果
    @GET("/1/classes/RemoteCookBook/")
    suspend fun getCountOfRemoteCookBook(
        @Query("where") where: String,
        @Query("count") count: Int = 1,
        @Query("limit") limit: Int = 0
    ): Count<RemoteCookBook>

    //获取所有菜谱
    @GET("/1/classes/RemoteCookBook/")
    suspend fun getAllOfCookBook(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int = 200
    ): ObjectList<RemoteCookBook>
}
