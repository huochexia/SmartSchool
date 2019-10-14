package com.goldenstraw.restaurant.goodsmanager.repositories

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * Created by Administrator on 2019/10/12 0012
 */

interface ILocalGoodsDataSource : ILocalDataSource {
    /**
     * 增加
     */
    fun addGoodsAll(list: MutableList<Goods>): Completable

    fun addCategoryAll(list: MutableList<GoodsCategory>): Completable

    fun addShoppingCartAll(list: MutableList<GoodsOfShoppingCart>): Completable

    fun insertShoppingCart(shoppingCart: GoodsOfShoppingCart): Completable
    /**
     * 插入
     */
    fun insertNewGoodsToLocal(goods: Goods): Completable

    fun insertCategoryToLocal(category: GoodsCategory): Completable
    /**
     * 获取
     */
    fun getAllCategory(): Observable<MutableList<GoodsCategory>>

    fun getGoodsOfCategory(code: String): Observable<MutableList<Goods>>

    fun findByName(name: String): Observable<MutableList<Goods>>
    /**
     * 删除
     */
    fun deleteGoodsFromLocal(goods: Goods): Completable

    fun deleteShoppingCartList(list: MutableList<GoodsOfShoppingCart>): Completable

    fun deletShoppingCart(shoppingCart: GoodsOfShoppingCart): Completable

    fun deleteCategoryFromLocal(category: GoodsCategory): Completable
    fun clearGoodsAll(): Completable
    fun clearCategoryAll(): Completable
}

class LocalGoodsDataSourceImpl(
    private val database: AppDatabase
) : ILocalGoodsDataSource {

    /**
     * 增加
     */
    override fun addGoodsAll(list: MutableList<Goods>): Completable {

        return database.goodsDao().insertGoodsList(list)
    }

    override fun addCategoryAll(list: MutableList<GoodsCategory>): Completable {

        return database.goodsDao().insertGoodsCategoryList(list)
    }

    override fun addShoppingCartAll(list: MutableList<GoodsOfShoppingCart>): Completable {
        return database.goodsDao().insertShoppingCartGoodsList(list)
    }

    /**
     * 插入,也可以用于修改
     */
    override fun insertCategoryToLocal(category: GoodsCategory): Completable {
        return database.goodsDao().insertNewCategory(category)
    }

    override fun insertNewGoodsToLocal(goods: Goods): Completable {
        return database.goodsDao().insertNewGoods(goods)
    }

    override fun insertShoppingCart(shoppingCart: GoodsOfShoppingCart): Completable {
        return database.goodsDao().insertShoppingCart(shoppingCart)
    }

    /**
     * 获取
     */
    override fun getAllCategory(): Observable<MutableList<GoodsCategory>> {

        return database.goodsDao().getAllCategory()

    }

    override fun getGoodsOfCategory(code: String): Observable<MutableList<Goods>> {

        return database.goodsDao().getAllGoodsOfCategory(code)

    }

    override fun findByName(name: String): Observable<MutableList<Goods>> {
        return database.goodsDao().findByName(name)
    }

    /**
     * 删除
     */
    override fun deleteCategoryFromLocal(category: GoodsCategory): Completable {
        return database.goodsDao().deleteCategory(category)
    }

    override fun deleteGoodsFromLocal(goods: Goods): Completable {
        return database.goodsDao().deleteGoods(goods)
    }

    override fun deletShoppingCart(shoppingCart: GoodsOfShoppingCart): Completable {
        return database.goodsDao().deleteShoppingCart(shoppingCart)
    }

    override fun deleteShoppingCartList(list: MutableList<GoodsOfShoppingCart>): Completable {
        return database.goodsDao().deleteShoppingCartList(list)
    }

    override fun clearCategoryAll(): Completable {
        return database.goodsDao().clearCategory()
    }

    override fun clearGoodsAll(): Completable {
        return database.goodsDao().clearGoods()
    }

}