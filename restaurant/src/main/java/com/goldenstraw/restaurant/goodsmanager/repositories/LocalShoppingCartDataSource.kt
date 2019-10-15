package com.goldenstraw.restaurant.goodsmanager.repositories

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Observable

interface ILocalShoppingCartDataSource : ILocalDataSource {

    fun getAllGoods(): Observable<MutableList<GoodsOfShoppingCart>>
}

/**
 * 管理本地购物车
 */
class LocalShoppingCartDataSourceImpl(
    private val database: AppDatabase
) : ILocalShoppingCartDataSource {
    /*
     * 获取购物车内所有商品
     */
    override fun getAllGoods(): Observable<MutableList<GoodsOfShoppingCart>> {
        return database.goodsDao().getAllShoppingCart()
    }
}