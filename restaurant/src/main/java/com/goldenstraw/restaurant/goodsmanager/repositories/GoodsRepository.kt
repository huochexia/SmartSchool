package com.goldenstraw.restaurant.goodsmanager.repositories

import com.goldenstraw.restaurant.goodsmanager.http.entity.createObject
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.room.dao.CategoryAndAllGoods
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Flowable
import io.reactivex.Observable

/**
 * 商品数据源，需要处理来自本地和远程的数据，所以它要继承同时拥有两个数据源的类
 */
class GoodsDataSource(
    private val remote: IRemoteGoodsDataSource,
    private val local: ILocalGoodsDataSource
) : BaseRepositoryBoth<IRemoteGoodsDataSource, ILocalGoodsDataSource>(remote, local) {

    /*
     增加商品到远程数据库,成功后再保存本地库
     */
    fun addGoods(goods: Goods): Observable<createObject> {
        return remote.addGoods(goods)
            .flatMap {
                local.addGoods(goods)
                    .andThen(Observable.just(it))
            }

    }

    /*
    增加商品类别到远程数据库，成功后保存本地库
     */
    fun addGoodsCategory(category: GoodsCategory): Observable<createObject> {
        return remote.addCategory(category)
            .flatMap {
                local.addCategory(category)
                    .andThen(Observable.just(it))
            }
    }

    /*
      按类别查询商品
     */
    fun queryGoods(category: GoodsCategory): Flowable<MutableList<Goods>> {

        return local.getGoodsFromCategory(category)

    }

    /*
      获得所有类别及其所拥入商品
     */
    fun getCategory(): Flowable<MutableList<CategoryAndAllGoods>> {
        return local.getCategoryAllGoods()
    }
}