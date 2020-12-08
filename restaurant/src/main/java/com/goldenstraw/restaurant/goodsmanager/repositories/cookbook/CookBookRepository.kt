package com.goldenstraw.restaurant.goodsmanager.repositories.cookbook

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.CBGCrossRef
import com.owner.basemodule.room.entities.CookBookWithGoods
import com.owner.basemodule.room.entities.CookBooks
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.util.ReturnResult

/**
 * 对CookBook数据管理
 */
class CookBookRepository(
    private val remote: IRemoteCookBookDataSource,
    private val local: ILocalCookBookDataSource
) : BaseRepositoryBoth<IRemoteCookBookDataSource, ILocalCookBookDataSource>(remote, local) {

    /*
       从本地Room中进行商品模糊查询得到的结果状态,通过这个状态对象来获得最后结果
    */
    sealed class SearchedStatus {
        object None : SearchedStatus()
        class Error(val throwable: Throwable) : SearchedStatus()
        class Success<T>(val list: MutableList<T>) : SearchedStatus()
    }

    /**
     * 先将新菜谱增加到网络数据库，成功后再添加到本地。
     *
     */
    suspend fun createCookBook(newCookBook: NewCookBook): ReturnResult {
        val createObject = remote.createCookBook(newCookBook)
        return if (createObject.isSuccess()) {
            val cookbook = CookBooks(
                objectId = createObject.objectId!!,
                foodCategory = newCookBook.foodCategory,
                foodKind = newCookBook.foodKind,
                foodName = newCookBook.foodName,
                usedNumber = newCookBook.usedNumber,
                isStandby = false
            )
            local.addCookBooks(cookbook)
            ReturnResult.Success(cookbook)
        } else {
            ReturnResult.Failure(createObject.error!!)
        }
    }

    /**
     * 生成菜谱和商品之间的关联关系,同上一样，网络保存得到objectId后，保存到本地
     */
    suspend fun createCrossRef(newRef: NewCrossRef): ReturnResult {
        val createObject = remote.createCrossRef(newRef)
        return if (createObject.isSuccess()) {
            val crossRef = CBGCrossRef(
                createObject.objectId!!,
                newRef.cb_id,
                newRef.goods_id,
                newRef.foodCategory
            )
            local.addCrossRef(crossRef)
            ReturnResult.Success(crossRef)
        } else {
            ReturnResult.Failure(createObject.error!!)
        }
    }

    suspend fun createDailyMeal(newDailyMeal: NewDailyMeal): CreateObject {
        return remote.createDailyMeal(newDailyMeal)
    }

    /*
    删除菜谱，先删除网络，成功了，删除相应的关联
     */
    suspend fun deleteCookBook(objectId: String): DeleteObject {
        return remote.deleteCookBook(objectId)
    }

    suspend fun deleteDailyMeal(objectId: String): DeleteObject {
        return remote.deleteDailyMeal(objectId)
    }

    suspend fun deleteCrossRef(objectId: String): DeleteObject {
        return remote.deleteCrossRef(objectId)
    }

    suspend fun deleteLocalCookBookAndRef(cookbook: CookBooks) {
        local.deleteCookBook(cookbook)
        local.deleteCrossRef(cookbook.objectId)
    }

    /*
    更新
     */
    suspend fun updateCookBook(newCookBook: NewCookBook, objectId: String): UpdateObject {
        return remote.updateCookBook(newCookBook, objectId)
    }

    suspend fun updateDailyMeal(newDailyMeal: UpdateIsteacher, objectId: String): UpdateObject {
        return remote.updateDailyMeal(newDailyMeal, objectId)
    }

    /*
      更新菜谱状态为常用或备用
     */
    suspend fun updateCookBookState(newCookBook: UpdateIsStandby, objectId: String): UpdateObject {
        return remote.updateCookBookState(newCookBook, objectId)
    }
    suspend fun addCookBookToLocal(cookbook: CookBooks){
        local.addCookBooks(cookbook)
    }

    /*
    更新菜谱使用次数
     */
    suspend fun updateNumberOfUsed(newCookBook: UpdateUsedNumber, objectId: String): UpdateObject {
        return remote.updateNumberOfUsed(newCookBook, objectId)
    }

    /*
    查询,从本地根据分类获取菜谱和相应的商品
     */
    suspend fun getCookBookWithGoodsOfCategory(
        category: String,
        isStandby: Boolean
    ): MutableList<CookBookWithGoods> {
        return local.getAllCookBookWithGoods(category, isStandby)
    }

    suspend fun getCookBookWithGoods(objectId: String): CookBookWithGoods {
        return local.getCookBookWithGoods(objectId)
    }

    suspend fun getCookBookOfCategory(where: String,skip:Int): ObjectList<CookBooks> {
        return remote.getCookBookOfCategory(where,skip)
    }

    suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal> {
        return remote.getDailyMealOfDate(where)
    }

    suspend fun getCookBookGoodsCrossRef(where: String,skip:Int): ObjectList<CBGCrossRef> {
        return remote.getCookBookGoodsCrossRef(where,skip)
    }


    /*
    模糊查询
     */
    suspend fun searchMaterial(name: String): MutableList<Goods> {
        return local.searchMaterial(name)
    }
    suspend fun searchCookBook(name:String,category: String):MutableList<CookBookWithGoods>{
        return local.searchCookBookWithGoods(name,category)
    }
    /*
    增加菜谱列表到本地
     */
    suspend fun addCookBookToLocal(list: MutableList<CookBooks>) {
        local.addAllCookBooksOfCategory(list)
    }

    /*
    增加关系列表到本地
     */
    suspend fun addCrossRefToLocal(refList: MutableList<CBGCrossRef>) {
        local.addCrossRefAllOfCategory(refList)
    }

    /*
    清空本地某类别菜谱
     */
    suspend fun clearCookBookOfCategory(category: String) {
        local.deleteCookBookOfCategory(category)
    }

    suspend fun clearCrossRefOfCategory(category: String) {
        local.deleteCrossRefOfCategory(category)
    }
}