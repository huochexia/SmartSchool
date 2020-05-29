package com.goldenstraw.restaurant.goodsmanager.http.manager.cookbok

import androidx.paging.DataSource.Factory
import com.goldenstraw.restaurant.goodsmanager.http.entities.CookBook
import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewDailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.UpdateIsteacher
import com.goldenstraw.restaurant.goodsmanager.http.service.CookBookApi
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import kotlinx.coroutines.Deferred

/**
 * @serviceApi 网络数据管理API
 */
class CookBookServiceManagerImpl(private val serviceApi: CookBookApi) : ICookBookServiceManager {

    override suspend fun createCookBook(newCookBook: CookBook): CreateObject {
        return serviceApi.createCookBook(newCookBook)
    }

    override suspend fun createDailyMeal(newDailyMeal: NewDailyMeal): CreateObject {
        return serviceApi.createDailyMeal(newDailyMeal)
    }

    override suspend fun deleteCookBook(objectId: String): DeleteObject {
        return serviceApi.deleteCookBook(objectId)
    }

    override suspend fun deleteDailyMeal(objectId: String): DeleteObject {
        return serviceApi.deleteDailyMeal(objectId)
    }

    override suspend fun updateCookBook(newCookBook: CookBook, objectId: String): UpdateObject {
        return serviceApi.updateCookBook(newCookBook, objectId)
    }

    override suspend fun updateDailyMeal(
        newDailyMeal: UpdateIsteacher,
        objectId: String
    ): UpdateObject {
        return serviceApi.updateDailyMeal(newDailyMeal, objectId)
    }

    override fun getCookBookOfCategory(where: String): Factory<Int, CookBook> {
        return serviceApi.getCookBookOfCategory(where)
    }

    override suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal> {
        return serviceApi.getDailyMealOfDate(where)
    }


}