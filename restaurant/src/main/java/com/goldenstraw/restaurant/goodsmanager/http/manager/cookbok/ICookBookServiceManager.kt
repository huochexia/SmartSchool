package com.goldenstraw.restaurant.goodsmanager.http.manager.cookbok

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.*
import com.owner.basemodule.room.entities.CBGCrossRef
import com.owner.basemodule.room.entities.CookBooks

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
    suspend fun updateCookBook(newCookBook: NewCookBook, objectId: String): UpdateObject
    suspend fun updateDailyMeal(newDailyMeal: UpdateIsteacher, objectId: String): UpdateObject
    suspend fun updateCookBookState(newCookBook: UpdateIsStandby,objectId: String):UpdateObject
    suspend fun updateUsedOfNumber(newCookBook:UpdateUsedNumber,objectId: String):UpdateObject

    /*
    查询
     */
    suspend fun getCookBookOfCategory(where: String,skip: Int): ObjectList<CookBooks>
    suspend fun getCookBooks(objectId: String):CookBooks
    suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal>
    suspend fun getCookBookGoodsCrossRef(where: String,skip:Int): ObjectList<CBGCrossRef>

}