package com.goldenstraw.restaurant.goodsmanager.http.manager.cookbok

import androidx.paging.DataSource.Factory
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.http.service.CookBookApi
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.CookBooks
import kotlinx.coroutines.Deferred

/**
 * @serviceApi 网络数据管理API
 */
class CookBookServiceManagerImpl(private val serviceApi: CookBookApi) : ICookBookServiceManager {

    override suspend fun createCookBook(newCookBook: NewCookBook): CreateObject {
        return serviceApi.createCookBook(newCookBook)
    }

    override suspend fun createDailyMeal(newDailyMeal: NewDailyMeal): CreateObject {
        return serviceApi.createDailyMeal(newDailyMeal)
    }

    override suspend fun createCrossRef(newRef: NewCrossRef): CreateObject {
        return serviceApi.createCrossRef(newRef)
    }

    override suspend fun deleteCookBook(objectId: String): DeleteObject {
        return serviceApi.deleteCookBook(objectId)
    }

    override suspend fun deleteCrossRef(objectId: String): DeleteObject {
        return serviceApi.deleteCrossRef(objectId)
    }

    override suspend fun deleteDailyMeal(objectId: String): DeleteObject {
        return serviceApi.deleteDailyMeal(objectId)
    }

    override suspend fun updateCookBook(newCookBook: CookBooks, objectId: String): UpdateObject {
        return serviceApi.updateCookBook(newCookBook, objectId)
    }

    override suspend fun updateDailyMeal(
        newDailyMeal: UpdateIsteacher,
        objectId: String
    ): UpdateObject {
        return serviceApi.updateDailyMeal(newDailyMeal, objectId)
    }

    override suspend fun getCookBookOfCategory(where: String): ObjectList<CookBooks> {
        return serviceApi.getCookBookOfCategory(where)
    }

    override suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal> {
        return serviceApi.getDailyMealOfDate(where)
    }


}