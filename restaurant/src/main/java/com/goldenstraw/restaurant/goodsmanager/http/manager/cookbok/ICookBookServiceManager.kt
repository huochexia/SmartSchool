package com.goldenstraw.restaurant.goodsmanager.http.manager.cookbok

import androidx.paging.DataSource
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.CookBooks
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
    suspend fun createCrossRef(newRef:NewCrossRef):CreateObject
    /*
    删除
     */
    suspend fun deleteCookBook(objectId: String): DeleteObject
    suspend fun deleteDailyMeal(objectId: String): DeleteObject
    suspend fun deleteCrossRef(objectId: String):DeleteObject

    /*
    更新
     */
    suspend fun updateCookBook(newCookBook: CookBooks, objectId: String): UpdateObject
    suspend fun updateDailyMeal(newDailyMeal: UpdateIsteacher, objectId: String): UpdateObject

    /*
    查询
     */
    suspend fun getCookBookOfCategory(where: String): ObjectList<CookBooks>
    suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal>
}