package com.goldenstraw.restaurant.goodsmanager.repositories

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.dao.CategoryAndAllGoods
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable


/**
 * 本地数据管理部分，定义接口，继承基本接口，定义自己的方法
 */

interface ILocalGoodsDataSource : ILocalDataSource {

    //获取类别及其所拥有的商品列表
    fun getCategoryAllGoods(): Observable<MutableList<CategoryAndAllGoods>>

    //获取购物车中所有商品
    fun getShoppingCartAllGoods(): Observable<MutableList<GoodsOfShoppingCart>>

    //通过名称的部分获取商品(模糊查询）
    fun getGoods(goodsName: String): Observable<MutableList<Goods>>

    //获取所有类别
    fun getAllCategory(): Observable<MutableList<GoodsCategory>>

    //按类别获取商品
    fun getGoodsFromCategory(category: GoodsCategory): Observable<MutableList<Goods>>

    //修改购物车某商品的数量
    fun updateGoodsQuantity(goodsName: String, quantity: Float): Completable

    //修改商品属性
    fun updateGoods(goods: Goods): Completable

    //修改商品类别
    fun updateGoodsCategory(category: GoodsCategory): Completable

    //删除商品
    fun deleteGoods(goods: Goods): Completable

    //删除类别
    fun deleteCategory(category: GoodsCategory): Completable

    //从购物车中删除商品
    fun removeShoppingCart(shoppingCartGoods: GoodsOfShoppingCart): Completable

    //增加商品类别
    fun addCategory(goodsCategory: GoodsCategory): Completable

    //增加商品
    fun addGoods(goods: Goods): Completable

    //加入购物车
    fun addGoodsToShoppingCart(shoppingCartGoods: GoodsOfShoppingCart): Completable
}

/**
 * 具体商品操作实现类
 */

class LocalGoodsDateSourceImpl(private val database: AppDatabase) : ILocalGoodsDataSource {

    override fun getCategoryAllGoods(): Observable<MutableList<CategoryAndAllGoods>> {
        return database.goodsDao().loadCategory()
    }

    override fun getShoppingCartAllGoods(): Observable<MutableList<GoodsOfShoppingCart>> {
        return database.goodsDao().getShoppingCartAllGoods()
    }

    override fun getGoods(goodsName: String): Observable<MutableList<Goods>> {
        return database.goodsDao().searchGoods(goodsName)
    }

    override fun getAllCategory(): Observable<MutableList<GoodsCategory>> {
        return database.goodsDao().queryAllCategory()
    }

    override fun getGoodsFromCategory(category: GoodsCategory): Observable<MutableList<Goods>> {
        return database.goodsDao().queryGoodsFromCategory(category.code)
    }

    override fun updateGoodsQuantity(goodsName: String, quantity: Float): Completable {
        return database.goodsDao().updateOrderQuantity(goodsName, quantity)
    }

    override fun updateGoods(goods: Goods): Completable {
        return database.goodsDao().updateGoods(goods)
    }

    override fun updateGoodsCategory(category: GoodsCategory): Completable {
        return database.goodsDao().updateCategory(category)
    }

    override fun deleteGoods(goods: Goods): Completable {
        return database.goodsDao().deleteGoods(goods)
    }

    override fun deleteCategory(category: GoodsCategory): Completable {
        return database.goodsDao().deleteCategory(category)
    }

    override fun removeShoppingCart(shoppingCartGoods: GoodsOfShoppingCart): Completable {
        return database.goodsDao().moveShoppingCart(shoppingCartGoods)
    }

    override fun addCategory(goodsCategory: GoodsCategory): Completable {
        return database.goodsDao().insertCategory(goodsCategory)
    }

    override fun addGoods(goods: Goods): Completable {
        return database.goodsDao().insertGoods(goods)
    }

    override fun addGoodsToShoppingCart(shoppingCartGoods: GoodsOfShoppingCart): Completable {
        return database.goodsDao().insertShoppingCartGoods(shoppingCartGoods)
    }

}