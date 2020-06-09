package com.goldenstraw.restaurant.goodsmanager.repositories.goods_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCategory
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.goldenstraw.restaurant.goodsmanager.http.manager.goods_order.IGoodsServiceManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * 对商品的远程数据操作接口
 */
interface IRemoteGoodsDataSource : IRemoteDataSource {
    /**
     * 增加
     */
    fun addGoods(goods: NewGoods): Single<CreateObject>

    fun addCategory(category: NewCategory): Single<CreateObject>
    /**
     * 更新
     */
    fun updateGoods(goods: NewGoods, objectId: String): Completable

    fun updateCategory(category: NewCategory, objectId: String): Completable
    /**
     * 获取
     */
    fun getAllCategory(): Observable<MutableList<GoodsCategory>>

    fun getGoodsOfCategory(category: GoodsCategory): Observable<MutableList<Goods>>

    fun getAllGoods(): Observable<MutableList<Goods>>

    fun getAllSupplier(): Observable<MutableList<User>>

    /**
     * 删除
     */
    fun deleteGoods(goods: Goods): Completable

    fun deleteCategory(goodsCategory: GoodsCategory): Completable

    /**
     * 获取某一天菜单
     */
    fun getDailyMealOfDate(where: String): Observable<ObjectList<DailyMeal>>
}

/**
 * 远程数据操作实现类
 */
class RemoteGoodsDataSourceImpl(
    private val service: IGoodsServiceManager
) : IRemoteGoodsDataSource {
    /**
     * 获取
     */
    override fun getAllCategory(): Observable<MutableList<GoodsCategory>> {
        return service.getCategory()
    }

    override fun getGoodsOfCategory(category: GoodsCategory): Observable<MutableList<Goods>> {
        return service.getGoodsOfCategory(category)
    }

    override fun getAllGoods(): Observable<MutableList<Goods>> {
        return service.getAllGoods()
    }

    override fun getAllSupplier(): Observable<MutableList<User>> {
        return service.getAllSupplier()
    }
    /**
     * 删除
     */
    override fun deleteGoods(goods: Goods): Completable {
        return service.deleteGoods(goods)
    }

    override fun deleteCategory(goodsCategory: GoodsCategory): Completable {
        return service.deleteCategory(goodsCategory)
    }

    /**
     * 增加
     */
    override fun addGoods(goods: NewGoods): Single<CreateObject> {
        return service.addGoods(goods)
    }

    override fun addCategory(category: NewCategory): Single<CreateObject> {
        return service.addCategory(category)
    }

    /**
     * 更新
     */
    override fun updateGoods(goods: NewGoods, objectId: String): Completable {
        return service.updateGoods(goods, objectId)
    }

    override fun updateCategory(category: NewCategory, objectId: String): Completable {
        return service.updateCategory(category, objectId)
    }

    override fun getDailyMealOfDate(where: String): Observable<ObjectList<DailyMeal>> {
        return service.getCookBookOfDailyMeal(where)
    }
}