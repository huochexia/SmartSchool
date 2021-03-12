package com.goldenstraw.restaurant.goodsmanager.http.manager.cookbok

//import com.owner.basemodule.room.entities.CBGCrossRef
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject

/**
 * 访问远程数据库，管理数据
 */
interface ICookBookServiceManager {

    /*
      生成
     */
    suspend fun createDailyMeal(newDailyMeal: NewDailyMeal): CreateObject
    /*
    删除
     */
    suspend fun deleteCookBook(objectId: String): DeleteObject
    suspend fun deleteDailyMeal(objectId: String): DeleteObject

    /*
    更新
     */
    suspend fun updateCookBook(newCookBook: RemoteCookBook, objectId: String): UpdateObject
    suspend fun updateDailyMeal(newDailyMeal: UpdateIsteacher, objectId: String): UpdateObject
    suspend fun updateCookBookState(newCookBook: UpdateIsStandby,objectId: String):UpdateObject
    suspend fun updateUsedOfNumber(newCookBook:UpdateUsedNumber,objectId: String):UpdateObject

    /*
    查询
     */
    suspend fun getCookBookOfCategory(where: String,skip: Int): ObjectList<RemoteCookBook>
    suspend fun getCookBooks(objectId: String):RemoteCookBook
    suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal>

    suspend fun getCountOfRemoteCookBook(where: String): Count<RemoteCookBook>
    suspend fun getAllOfCookBook( skip: Int): ObjectList<RemoteCookBook>
}