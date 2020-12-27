package com.goldenstraw.restaurant.goodsmanager.repositories.cookbook

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.entities.CookBookWithMaterials
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.LocalCookBook
import com.owner.basemodule.room.entities.Material

interface ILocalCookBookDataSource : ILocalDataSource {
    //模糊查找
    suspend fun searchMaterial(name: String): MutableList<Goods>

    suspend fun searchCookBookWithMaterials(
        name: String,
        category: String
    ): MutableList<CookBookWithMaterials>

    //获得所有某分类所有菜谱
    suspend fun getAllCookBookWithMaterials(
        foodCategory: String,
        isStandby: Boolean
    ): MutableList<CookBookWithMaterials>

    suspend fun getCookBookWithMaterials(objectId: String): CookBookWithMaterials

    //增加菜谱
    suspend fun addCookBooks(cookbook: LocalCookBook)
    suspend fun addAllCookBooksOfCategory(cookbooks: MutableList<LocalCookBook>)
    suspend fun addMaterailOfCookBooks(materials: MutableList<Material>)


    //删除关系
    suspend fun deleteLocalCookBookAndMaterials(cm: CookBookWithMaterials)
    suspend fun deleteCookBookOfCategory(category: String)
    suspend fun deleteMaterialsOfCookBook(materials:MutableList<Material>)

}

class LocalCookBookDataSourceImpl(
    private val database: AppDatabase
) : ILocalCookBookDataSource {

    /*
        根据商品名称模糊查询
     */
    override suspend fun searchMaterial(name: String): MutableList<Goods> {
        return database.goodsDao().searchMaterial(name)
    }
    /*
       根据菜谱名称模糊查询
     */
    override suspend fun searchCookBookWithMaterials(
        name: String,
        category: String
    ): MutableList<CookBookWithMaterials> {
        return database.cookbookDao().queryCookBookAndMaterials(name, category)
    }

    /*
    获取某类菜谱
     */
    override suspend fun getAllCookBookWithMaterials(
        foodCategory: String,
        isStandby: Boolean
    ): MutableList<CookBookWithMaterials> {
        return database.cookbookDao().getAllCookBookAndMaterials(foodCategory, isStandby)
    }
    /*
    获取某个菜谱
     */
    override suspend fun getCookBookWithMaterials(objectId: String): CookBookWithMaterials {
        return database.cookbookDao().getCookBookAndMaterials(objectId)
    }


    /*
      增加
     */
    override suspend fun addCookBooks(cookbook: LocalCookBook) {
        database.cookbookDao().addCookBook(cookbook)
    }

    override suspend fun addAllCookBooksOfCategory(cookbooks: MutableList<LocalCookBook>) {
        database.cookbookDao().addAllCookBook(cookbooks)
    }

    override suspend fun addMaterailOfCookBooks(materials: MutableList<Material>) {
        database.cookbookDao().addMaterials(materials)
    }


    /*
    删除
     */
    override suspend fun deleteLocalCookBookAndMaterials(cm: CookBookWithMaterials) {
        database.cookbookDao().deleteCookBook(cm.cookbook)
        database.cookbookDao().deleteMaterialOfCookbook(cm.materials as MutableList<Material>)
    }

    override suspend fun deleteCookBookOfCategory(category: String) {
        database.cookbookDao().deleteCookBookOfCategory(category)
    }

    override suspend fun deleteMaterialsOfCookBook(materials: MutableList<Material>) {
        database.cookbookDao().deleteMaterialOfCookbook(materials)
    }
}