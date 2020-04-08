package com.goldenstraw.restaurant.goodsmanager.repositories.cookbook

import androidx.paging.DataSource
import androidx.paging.DataSource.Factory
import com.goldenstraw.restaurant.goodsmanager.http.entities.CookBook
import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCookBook
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewDailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.manager.cookbok.ICookBookServiceManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import kotlinx.coroutines.Deferred

/**
 * 访问远程数据接口
 */
interface IRemoteCookBookDataSource : IRemoteDataSource {
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

class RemoteCookBookDataSourceImpl(
    private val manager: ICookBookServiceManager
) : IRemoteCookBookDataSource {
    override suspend fun createCookBook(newCookBook: NewCookBook): CreateObject {
        return manager.createCookBook(newCookBook)
    }

    override suspend fun createDailyMeal(newDailyMeal: NewDailyMeal): CreateObject {
        return manager.createDailyMeal(newDailyMeal)
    }

    override suspend fun deleteCookBook(objectId: String): DeleteObject {
        return manager.deleteCookBook(objectId)
    }

    override suspend fun deleteDailyMeal(objectId: String): DeleteObject {
        return manager.deleteDailyMeal(objectId)
    }

    override suspend fun updateCookBook(newCookBook: NewCookBook, objectId: String): UpdateObject {
        return manager.updateCookBook(newCookBook, objectId)
    }

    override suspend fun updateDailyMeal(
        newDailyMeal: NewDailyMeal,
        objectId: String
    ): UpdateObject {
        return manager.updateDailyMeal(newDailyMeal, objectId)
    }

    override fun getCookBookOfCategory(where: String): Factory<Int, CookBook> {
        return manager.getCookBookOfCategory(where)
    }

    override fun getDailyMealOfDate(where: String): Deferred<ObjectList<DailyMeal>> {
        return manager.getDailyMealOfDate(where)
    }

}