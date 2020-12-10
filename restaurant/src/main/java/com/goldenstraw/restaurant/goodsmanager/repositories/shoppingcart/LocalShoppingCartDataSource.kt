package com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable
import io.reactivex.Observable

interface ILocalShoppingCartDataSource : ILocalDataSource {

    fun getAllGoods(): Observable<MutableList<GoodsOfShoppingCart>>

    fun deleteShoppingCartList(goodslist: MutableList<GoodsOfShoppingCart>): Completable

    fun deleteGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable

    fun updateGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable

    suspend fun getGoodsOfFoodCategory(foodCategory:String):MutableList<GoodsOfShoppingCart>
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

    override fun deleteShoppingCartList(goodslist: MutableList<GoodsOfShoppingCart>): Completable {
        return database.goodsDao().deleteShoppingCartList(goodslist)
    }

    override fun deleteGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable {
        return database.goodsDao().deleteGoodsOfShoppingCart(goods)
    }

    override fun updateGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable {
        return database.goodsDao().insertShoppingCart(goods)
    }

    override suspend fun getGoodsOfFoodCategory(foodCategory: String): MutableList<GoodsOfShoppingCart> {
        return database.goodsDao().getShoppingCartOfFoodCategory(foodCategory)
    }
}