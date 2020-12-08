package com.goldenstraw.restaurant.goodsmanager.repositories.cookbook

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.http.manager.cookbok.ICookBookServiceManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.CBGCrossRef
import com.owner.basemodule.room.entities.CookBooks

/**
 * 访问远程数据接口
 */
interface IRemoteCookBookDataSource : IRemoteDataSource {
    /*
    生成
     */
    suspend fun createCookBook(newCookBook: NewCookBook): CreateObject
    suspend fun createDailyMeal(newDailyMeal: NewDailyMeal): CreateObject
    suspend fun createCrossRef(newRef: NewCrossRef): CreateObject

    /*
    删除
     */
    suspend fun deleteCookBook(objectId: String): DeleteObject
    suspend fun deleteCrossRef(objectId: String): DeleteObject
    suspend fun deleteDailyMeal(objectId: String): DeleteObject

    /*
    更新
     */
    suspend fun updateCookBook(newCookBook: NewCookBook, objectId: String): UpdateObject
    suspend fun updateDailyMeal(newDailyMeal: UpdateIsteacher, objectId: String): UpdateObject
    suspend fun updateCookBookState(newCookBook: UpdateIsStandby, objectId: String): UpdateObject
    suspend fun updateNumberOfUsed(newCookBook: UpdateUsedNumber, objectId: String): UpdateObject

    /*
    查询
     */
    suspend fun getCookBookOfCategory(where: String, skip: Int): ObjectList<CookBooks>
    suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal>
    suspend fun getCookBookGoodsCrossRef(where: String, skip: Int): ObjectList<CBGCrossRef>

}

class RemoteCookBookDataSourceImpl(
    private val manager: ICookBookServiceManager
) : IRemoteCookBookDataSource {
    override suspend fun createCookBook(newCookBook: NewCookBook): CreateObject {
        return manager.createCookBook(newCookBook)
    }

    override suspend fun createDailyMeal(newDailyMeal: NewDailyMeal): CreateObject {
        return manager.createDailyMeal(newDailyMeal)
    }

    override suspend fun createCrossRef(newRef: NewCrossRef): CreateObject {
        return manager.createCrossRef(newRef)
    }


    override suspend fun deleteCookBook(objectId: String): DeleteObject {
        return manager.deleteCookBook(objectId)
    }

    override suspend fun deleteCrossRef(objectId: String): DeleteObject {
        return manager.deleteCrossRef(objectId)
    }

    override suspend fun deleteDailyMeal(objectId: String): DeleteObject {
        return manager.deleteDailyMeal(objectId)
    }

    override suspend fun updateCookBook(newCookBook: NewCookBook, objectId: String): UpdateObject {
        return manager.updateCookBook(newCookBook, objectId)
    }

    override suspend fun updateDailyMeal(
        newDailyMeal: UpdateIsteacher,
        objectId: String
    ): UpdateObject {
        return manager.updateDailyMeal(newDailyMeal, objectId)
    }

    override suspend fun updateCookBookState(
        newCookBook: UpdateIsStandby,
        objectId: String
    ): UpdateObject {
        return manager.updateCookBookState(newCookBook, objectId)
    }

    override suspend fun updateNumberOfUsed(
        newCookBook: UpdateUsedNumber,
        objectId: String
    ): UpdateObject {
        return manager.updateUsedOfNumber(newCookBook, objectId)
    }

    override suspend fun getCookBookOfCategory(where: String, skip: Int): ObjectList<CookBooks> {
        return manager.getCookBookOfCategory(where, skip)
    }

    override suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal> {
        return manager.getDailyMealOfDate(where)
    }

    override suspend fun getCookBookGoodsCrossRef(
        where: String,
        skip: Int
    ): ObjectList<CBGCrossRef> {
        return manager.getCookBookGoodsCrossRef(where,skip)
    }


}