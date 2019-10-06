package com.goldenstraw.restaurant.goodsmanager.repositories

import com.goldenstraw.restaurant.goodsmanager.http.entity.NewObject
import com.goldenstraw.restaurant.goodsmanager.http.entity.objectList
import com.goldenstraw.restaurant.goodsmanager.http.manager.IGoodsServiceManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.network.HttpResult
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
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
    fun addGoods(goods: Goods): Single<NewObject>

    fun addCategory(category: GoodsCategory): Single<NewObject>
    /**
     * 更新
     */
    fun updateGoods(goods: Goods): Completable

    fun updateCategory(category: GoodsCategory): Completable
    /**
     * 获取
     */
    fun getAllCategory(): Observable<HttpResult<objectList<GoodsCategory>>>

    fun getGoodsOfCategory(category: GoodsCategory): Observable<HttpResult<objectList<Goods>>>
    /**
     * 删除
     */
    fun deleteGoods(goods: Goods): Completable

    fun deleteCategory(goodsCategory: GoodsCategory): Completable
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
    override fun getAllCategory(): Observable<HttpResult<objectList<GoodsCategory>>> {
        return service.getCategory()
    }

    override fun getGoodsOfCategory(category: GoodsCategory): Observable<HttpResult<objectList<Goods>>> {
        return service.getGoodsOfCategory(category)
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
    override fun addGoods(goods: Goods): Single<NewObject> {
        return service.addGoods(goods)
    }

    override fun addCategory(category: GoodsCategory): Single<NewObject> {
        return service.addCategory(category)
    }

    /**
     * 更新
     */
    override fun updateGoods(goods: Goods): Completable {
        return service.updateGoods(goods)
    }

    override fun updateCategory(category: GoodsCategory): Completable {
        return service.updateCategory(category)
    }

}