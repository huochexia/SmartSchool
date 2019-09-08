package com.goldenstraw.restaurant.goodsmanager.repositories

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.dao.CategoryAndAllGoods
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.ShoppingCartGoods
import io.reactivex.Completable
import io.reactivex.Flowable



/**
 * 本地数据管理部分，定义接口，继承基本接口，定义自己的方法
 */

interface ILocalGoodsDataSource : ILocalDataSource {

    //获取类别及其所拥有的商品列表
    fun getCategoryAllGoods(): Flowable<MutableList<CategoryAndAllGoods>>

    //获取购物车中所有商品
    fun getShoppingCartAllGoods(): Flowable<MutableList<ShoppingCartGoods>>

    //通过名称的部分获取商品(模糊查询）
    fun getGoods(goodsName: String): Flowable<MutableList<Goods>>

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
    fun removeShoppingCart(shoppingCartGoods: ShoppingCartGoods): Completable

    //增加商品类别
    fun addCategory(goodsCategory: GoodsCategory): Completable

    //增加商品
    fun addGoods(goods: Goods): Completable

    //加入购物车
    fun addGoodsToShoppingCart(shoppingCartGoods: ShoppingCartGoods): Completable
}

/**
 * 具体商品操作实现类
 */

class LocalGoodsDateSourceImpl (private val database: AppDatabase) : ILocalGoodsDataSource {

    override fun getCategoryAllGoods(): Flowable<MutableList<CategoryAndAllGoods>> {
        return database.goodsDao().loadCategory()
    }

    override fun getShoppingCartAllGoods(): Flowable<MutableList<ShoppingCartGoods>> {
        return database.goodsDao().getShoppingCartAllGoods()
    }

    override fun getGoods(goodsName: String): Flowable<MutableList<Goods>> {
        return database.goodsDao().searchGoods(goodsName)
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

    override fun removeShoppingCart(shoppingCartGoods: ShoppingCartGoods): Completable {
        return database.goodsDao().moveShoppingCart(shoppingCartGoods)
    }

    override fun addCategory(goodsCategory: GoodsCategory): Completable {
        return database.goodsDao().insertCategory(goodsCategory)
    }

    override fun addGoods(goods: Goods): Completable {
        return database.goodsDao().insertGoods(goods)
    }

    override fun addGoodsToShoppingCart(shoppingCartGoods: ShoppingCartGoods): Completable {
        return database.goodsDao().insertShoppingCartGoods(shoppingCartGoods)
    }

}