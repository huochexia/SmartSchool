package com.goldenstraw.restaurant.goodsmanager.repositories

import com.goldenstraw.restaurant.goodsmanager.http.entity.createObject
import com.goldenstraw.restaurant.goodsmanager.http.manager.GoodsServiceManagerImpl
import com.goldenstraw.restaurant.goodsmanager.http.manager.IGoodsServiceManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * 对商品的远程数据操作接口
 */
interface IRemoteGoodsDataSource : IRemoteDataSource {
    /*
     *增加商品
     */
    fun addGoods(goods: Goods): Observable<createObject>

    /*
     *增加类别
     */
    fun addCategory(category: GoodsCategory): Observable<createObject>
}

/**
 * 远程数据操作实现类
 */
class RemoteGoodsDataSoureImpl(
    private val service: IGoodsServiceManager
) : IRemoteGoodsDataSource {

    override fun addGoods(goods: Goods): Observable<createObject> {
        return service.addGoods(goods)
            .subscribeOn(Schedulers.io())
    }

    override fun addCategory(category: GoodsCategory): Observable<createObject> {
        return service.addCategory(category).subscribeOn(Schedulers.io())
    }

}