package com.goldenstraw.restaurant.goodsmanager.repositories.cookbook

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.entities.Goods

interface ILocalCookBookDataSource : ILocalDataSource {
    //模糊查找
    suspend fun searchMaterial(name: String): MutableList<Goods>
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

}