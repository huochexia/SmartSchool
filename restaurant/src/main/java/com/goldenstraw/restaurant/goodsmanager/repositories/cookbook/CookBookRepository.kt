package com.goldenstraw.restaurant.goodsmanager.repositories.cookbook

import androidx.paging.DataSource
import com.goldenstraw.restaurant.goodsmanager.http.entities.CookBook
import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCookBook
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewDailyMeal
import com.owner.basemodule.base.repository.BaseRepositoryRemote
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import kotlinx.coroutines.Deferred

/**
 * 对CookBook数据管理
 */
class CookBookRepository(
    private val remote: IRemoteCookBookDataSource
) : BaseRepositoryRemote<IRemoteCookBookDataSource>(remote) {

    /*
      生成
     */
    suspend fun createCookBook(newCookBook: NewCookBook): CreateObject {
        return remote.createCookBook(newCookBook)
    }

    suspend fun createDailyMeal(newDailyMeal: NewDailyMeal): CreateObject {
        return remote.createDailyMeal(newDailyMeal)
    }

    /*
    删除
     */
    suspend fun deleteCookBook(objectId: String): DeleteObject {
        return remote.deleteCookBook(objectId)
    }

    suspend fun deleteDailyMeal(objectId: String): DeleteObject {
        return remote.deleteDailyMeal(objectId)
    }

    /*
    更新
     */
    suspend fun updateCookBook(newCookBook: NewCookBook, objectId: String): UpdateObject {
        return remote.updateCookBook(newCookBook, objectId)
    }

    suspend fun updateDailyMeal(newDailyMeal: NewDailyMeal, objectId: String): UpdateObject {
        return remote.updateDailyMeal(newDailyMeal, objectId)
    }

    /*
    查询
     */
    fun getCookBookOfCategory(where: String): DataSource.Factory<Int, CookBook> {
        return remote.getCookBookOfCategory(where)
    }

    fun getDailyMealOfDate(where: String): Deferred<ObjectList<DailyMeal>> {
        return remote.getDailyMealOfDate(where)
    }
}