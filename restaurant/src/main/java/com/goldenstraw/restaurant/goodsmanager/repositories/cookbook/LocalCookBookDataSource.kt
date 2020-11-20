package com.goldenstraw.restaurant.goodsmanager.repositories.cookbook

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.entities.CBGCrossRef
import com.owner.basemodule.room.entities.CookBookWithGoods
import com.owner.basemodule.room.entities.CookBooks
import com.owner.basemodule.room.entities.Goods
import kotlinx.coroutines.flow.Flow

interface ILocalCookBookDataSource : ILocalDataSource {
    //模糊查找
    suspend fun searchMaterial(name: String): MutableList<Goods>
    suspend fun searchCookBookWithGoods(name: String,category: String): MutableList<CookBookWithGoods>

    //获得所有某分类所有菜谱
    fun getAllCookBookWithGoods(foodCategory: String):Flow<MutableList<CookBookWithGoods>>
    suspend fun getCookBookWithGoods(objectId: String): CookBookWithGoods

    //增加菜谱
    suspend fun addCookBooks(cookbook: CookBooks)
    suspend fun addAllCookBooksOfCategory(cookbooks: MutableList<CookBooks>)

    //增加关系
    suspend fun addCrossRef(newRef: CBGCrossRef)
    suspend fun addCrossRefAllOfCategory(refs: MutableList<CBGCrossRef>)

    //删除关系
    suspend fun deleteCookBook(cookbook: CookBooks)
    suspend fun deleteCookBookOfCategory(category: String)
    suspend fun deleteCrossRef(cookbookId: String)
    suspend fun deleteCrossRefOfCategory(category: String)
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
    override suspend fun searchCookBookWithGoods(name: String,category: String): MutableList<CookBookWithGoods> {
        return database.cookbookDao().queryCookBookWithGoods(name,category)
    }

    /*
    获取某分类的所有菜谱
     */
    override  fun getAllCookBookWithGoods(foodCategory: String): Flow<MutableList<CookBookWithGoods>> {
        return database.cookbookDao().getAllCookBookWithGoods(foodCategory)
    }

    override suspend fun getCookBookWithGoods(objectId: String): CookBookWithGoods {
        return database.cookbookDao().getCookBookWithGoods(objectId)
    }

    /*
      增加
     */
    override suspend fun addCookBooks(cookbook: CookBooks) {
        database.cookbookDao().addCookBook(cookbook)
    }

    override suspend fun addAllCookBooksOfCategory(cookbooks: MutableList<CookBooks>) {
        database.cookbookDao().addAllCookBook(cookbooks)
    }


    override suspend fun addCrossRef(newRef: CBGCrossRef) {
        database.cookbookDao().addCrossRef(newRef)
    }

    override suspend fun addCrossRefAllOfCategory(refs: MutableList<CBGCrossRef>) {
        database.cookbookDao().addAllCrossRef(refs)
    }

    /*
    删除
     */
    override suspend fun deleteCookBook(cookbook: CookBooks) {
        database.cookbookDao().deleteCookBook(cookbook)
    }

    override suspend fun deleteCookBookOfCategory(category: String) {
        database.cookbookDao().deleteCookBookOfCategory(category)
    }

    override suspend fun deleteCrossRef(cookbookId: String) {
        database.cookbookDao().deleteCrossRef(cookbookId)
    }

    override suspend fun deleteCrossRefOfCategory(category: String) {
        database.cookbookDao().deleteCrossRefOfCategory(category)
    }
}