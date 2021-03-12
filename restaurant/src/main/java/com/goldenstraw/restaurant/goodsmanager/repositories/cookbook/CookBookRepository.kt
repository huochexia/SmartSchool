package com.goldenstraw.restaurant.goodsmanager.repositories.cookbook

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.CookBookWithMaterials
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.LocalCookBook
import com.owner.basemodule.room.entities.Material

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


    suspend fun createDailyMeal(newDailyMeal: NewDailyMeal): CreateObject {
        return remote.createDailyMeal(newDailyMeal)
    }

    /*
    删除菜谱，先删除网络，成功了，删除相应的关联
     */
    suspend fun deleteRemoteCookBook(objectId: String): DeleteObject {
        return remote.deleteCookBook(objectId)
    }

    suspend fun deleteLocalCookBookWithMaterials(cm: CookBookWithMaterials) {
        local.deleteLocalCookBookAndMaterials(cm)
    }

    suspend fun deleteMaterialsOfCookBook(material: MutableList<Material>){
        local.deleteMaterialsOfCookBook(material)
    }


    suspend fun deleteDailyMeal(objectId: String): DeleteObject {
        return remote.deleteDailyMeal(objectId)
    }


    /*
    更新
     */
    suspend fun updateCookBook(newCookBook: RemoteCookBook, objectId: String): UpdateObject {
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

    suspend fun addCookBookToLocal(cookbook: LocalCookBook) {
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
    suspend fun getCookBookWithMaterialOfCategory(
        category: String,
        isStandby: Boolean
    ): MutableList<CookBookWithMaterials> {
        return local.getAllCookBookWithMaterials(category, isStandby)
    }

    suspend fun getCookbook(objectId: String): RemoteCookBook {
        return remote.getCookBook(objectId)
    }

    suspend fun getCookBookWithMaterials(objectId: String): CookBookWithMaterials {
        return local.getCookBookWithMaterials(objectId)
    }

    suspend fun getCookBookOfCategory(where: String, skip: Int): ObjectList<RemoteCookBook> {
        return remote.getCookBookOfCategory(where, skip)
    }

    suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal> {
        return remote.getDailyMealOfDate(where)
    }


    /*
    模糊查询
     */
    suspend fun searchMaterial(name: String): MutableList<Goods> {
        return local.searchMaterial(name)
    }

    suspend fun searchCookBook(name: String, category: String): MutableList<CookBookWithMaterials> {
        return local.searchCookBookWithMaterials(name, category)
    }

    /*
    增加菜谱列表到本地
     */
    suspend fun addCookBooksToLocal(list: MutableList<LocalCookBook>) {
        local.addAllCookBooksOfCategory(list)

    }

    suspend fun addMaterialOfCookBooks(list: MutableList<Material>) {
        local.addMaterailOfCookBooks(list)
    }




    /*
    清空本地数据
     */
    suspend fun clearLocalCookBook() {
        local.clearCookBook()
    }

    /*
    获取远程数据
     */
    suspend fun getAllOfRemoteCookBook(skip: Int): ObjectList<RemoteCookBook> {

        return remote.getAllOfCookBook(skip)
    }
}