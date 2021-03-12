package com.goldenstraw.restaurant.goodsmanager.http.manager.cookbok

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.http.service.CookBookApi
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject

/**
 * @serviceApi 网络数据管理API
 */
class CookBookServiceManagerImpl(private val serviceApi: CookBookApi) : ICookBookServiceManager {


    override suspend fun createDailyMeal(newDailyMeal: NewDailyMeal): CreateObject {
        return serviceApi.createDailyMeal(newDailyMeal)
    }



    override suspend fun deleteCookBook(objectId: String): DeleteObject {
        return serviceApi.deleteCookBook(objectId)
    }



    override suspend fun deleteDailyMeal(objectId: String): DeleteObject {
        return serviceApi.deleteDailyMeal(objectId)
    }

    override suspend fun updateCookBook(newCookBook: RemoteCookBook, objectId: String): UpdateObject {
        return serviceApi.updateCookBook(newCookBook, objectId)
    }

    override suspend fun updateDailyMeal(
        newDailyMeal: UpdateIsteacher,
        objectId: String
    ): UpdateObject {
        return serviceApi.updateDailyMeal(newDailyMeal, objectId)
    }

    override suspend fun updateCookBookState(
        newCookBook: UpdateIsStandby,
        objectId: String
    ): UpdateObject {
        return serviceApi.updateCookBookState(newCookBook,objectId)
    }

    override suspend fun updateUsedOfNumber(newCookBook: UpdateUsedNumber,objectId: String): UpdateObject {
        return serviceApi.updateNumberOfUsed(newCookBook,objectId)
    }

    override suspend fun getCookBookOfCategory(where: String,skip: Int): ObjectList<RemoteCookBook> {
        return serviceApi.getCookBookOfCategory(where, skip)
    }

    override suspend fun getCookBooks(objectId: String): RemoteCookBook {
        return serviceApi.getCookBook(objectId)
    }

    override suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal> {
        return serviceApi.getDailyMealOfDate(where)
    }

    override suspend fun getCountOfRemoteCookBook(where: String): Count<RemoteCookBook> {
        return serviceApi.getCountOfRemoteCookBook(where)
    }

    override suspend fun getAllOfCookBook(skip: Int): ObjectList<RemoteCookBook> {
        return serviceApi.getAllOfCookBook(skip)
    }
}