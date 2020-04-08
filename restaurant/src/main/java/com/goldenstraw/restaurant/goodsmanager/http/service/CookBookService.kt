package com.goldenstraw.restaurant.goodsmanager.http.service

import androidx.paging.DataSource
import com.goldenstraw.restaurant.goodsmanager.http.entities.CookBook
import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCookBook
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewDailyMeal
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import kotlinx.coroutines.Deferred
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
    @POST("/1/classes/CookBook")
    suspend fun createCookBook(@Body newCookBook: NewCookBook): CreateObject

    //增加每日菜单
    @POST("/1/classes/DailyMeal")
    suspend fun createDailyMeal(@Body dailyMeal: NewDailyMeal): CreateObject

    /*
     删除
     */
    //删除菜谱
    @DELETE("/1/classes/CookBook/{objectId}")
    suspend fun deleteCookBook(@Path("objectId") objectId: String): DeleteObject

    //删除每日菜单
    @DELETE("/1/classes/DailyMeal/{objectId}")
    suspend fun deleteDailyMeal(@Path("objectId") objectId: String): DeleteObject

    /*
      修改
     */
    //修改菜谱
    @PUT("/1/classes/CookBook/{objectId}")
    suspend fun updateCookBook(
        @Body newCookBook: NewCookBook,
        @Path("objectId") objectId: String
    ): UpdateObject

    //修改每日菜单
    @PUT("/1/classes/DailyMeal/{objectId}")
    suspend fun updateDailyMeal(
        @Body newDailyMeal: NewDailyMeal,
        @Path("objectId") objectId: String
    ): UpdateObject

    /*
      查询
     */

    //查询某日的菜单,使用协程
    @GET("/1/classes/DailyMeal/")
    fun getDailyMealOfDate(@Query("where") where: String): Deferred<ObjectList<DailyMeal>>

    //查询某类菜谱,这里使用PagedList
    @GET("/1/classes/CookBook/")
    fun getCookBookOfCategory(
        @Query("where") where: String,
        @Query("limit") limit: Int = 500
    ): DataSource.Factory<Int, CookBook>
}
