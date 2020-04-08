package com.goldenstraw.restaurant.goodsmanager.http.manager.cookbok

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

/**
 * 访问远程数据库，管理数据
 */
interface ICookBookServiceManager {

    /*
      生成
     */
    suspend fun createCookBook(newCookBook: NewCookBook): CreateObject
    suspend fun createDailyMeal(newDailyMeal: NewDailyMeal): CreateObject

    /*
    删除
     */
    suspend fun deleteCookBook(objectId: String): DeleteObject
    suspend fun deleteDailyMeal(objectId: String): DeleteObject

    /*
    更新
     */
    suspend fun updateCookBook(newCookBook: NewCookBook, objectId: String): UpdateObject
    suspend fun updateDailyMeal(newDailyMeal: NewDailyMeal, objectId: String): UpdateObject

    /*
    查询
     */
    fun getCookBookOfCategory(where: String): DataSource.Factory<Int, CookBook>
    fun getDailyMealOfDate(where: String): Deferred<ObjectList<DailyMeal>>
}