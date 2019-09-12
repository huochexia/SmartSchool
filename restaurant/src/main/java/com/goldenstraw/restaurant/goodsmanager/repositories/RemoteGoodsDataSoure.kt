package com.goldenstraw.restaurant.goodsmanager.repositories

import com.goldenstraw.restaurant.goodsmanager.http.entity.newObject
import com.goldenstraw.restaurant.goodsmanager.http.manager.IGoodsServiceManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.network.HttpResult
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * 对商品的远程数据操作接口
 */
interface IRemoteGoodsDataSource : IRemoteDataSource {
    /**
     * 增加
     */
    fun addGoods(goods: Goods): Observable<HttpResult<newObject>>

    fun addCategory(category: GoodsCategory): Observable<HttpResult<newObject>>
    /**
     * 更新
     */
    fun updateGoods(goods: Goods): Completable

    fun updateCategory(category: GoodsCategory): Completable
}

/**
 * 远程数据操作实现类
 */
class RemoteGoodsDataSourceImpl(
    private val service: IGoodsServiceManager
) : IRemoteGoodsDataSource {
    /**
     * 增加
     */
    override fun addGoods(goods: Goods): Observable<HttpResult<newObject>> {
        return service.addGoods(goods)
    }

    override fun addCategory(category: GoodsCategory): Observable<HttpResult<newObject>> {
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