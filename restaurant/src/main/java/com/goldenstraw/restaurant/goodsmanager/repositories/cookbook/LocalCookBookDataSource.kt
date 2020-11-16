package com.goldenstraw.restaurant.goodsmanager.repositories.cookbook

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.entities.CookBookGoodsCrossRef
import com.owner.basemodule.room.entities.CookBookWithGoods
import com.owner.basemodule.room.entities.CookBooks
import com.owner.basemodule.room.entities.Goods
import kotlinx.coroutines.flow.Flow

interface ILocalCookBookDataSource : ILocalDataSource {
    //模糊查找
    suspend fun searchMaterial(name: String): MutableList<Goods>
    fun queryCookBookWithGoods(name: String): Flow<MutableList<CookBookWithGoods>>

    //获得所有某分类所有菜谱
    fun getCookBookWithGoods(foodCategory: String): Flow<MutableList<CookBookWithGoods>>


    //增加菜谱
    suspend fun addCookBooks(cookbook: CookBooks)
    suspend fun addAllCookBooksOfCategory(cookbooks: MutableList<CookBooks>)

    //增加关系
    suspend fun addCrossRef(newRef: CookBookGoodsCrossRef)
    suspend fun addCrossRefAllOfCategory(refs: MutableList<CookBookGoodsCrossRef>)

    //删除关系
    suspend fun deleteCrossRef(ref: CookBookGoodsCrossRef)
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
    override fun queryCookBookWithGoods(name: String): Flow<MutableList<CookBookWithGoods>> {
        return database.cookbookDao().queryCookBookWithGoods(name)
    }

    /*
    获取某分类的所有菜谱
     */
    override fun getCookBookWithGoods(foodCategory: String): Flow<MutableList<CookBookWithGoods>> {
        return database.cookbookDao().getAllCookBookWithGoods(foodCategory)
    }

    /*
      增加菜谱
     */
    override suspend fun addCookBooks(cookbook: CookBooks) {
        database.cookbookDao().addCookBook(cookbook)
    }

    override suspend fun addAllCookBooksOfCategory(cookbooks: MutableList<CookBooks>) {
        database.cookbookDao().addAllCookBook(cookbooks)
    }

    /*
      增加关系
     */
    override suspend fun addCrossRef(newRef: CookBookGoodsCrossRef) {
        database.cookbookDao().addCrossRef(newRef)
    }

    override suspend fun addCrossRefAllOfCategory(refs: MutableList<CookBookGoodsCrossRef>) {
        database.cookbookDao().addAllCrossRef(refs)
    }

    /*
      删除关系
     */
    override suspend fun deleteCrossRef(ref: CookBookGoodsCrossRef) {
        database.cookbookDao().deleteCrossRef(ref)
    }
}