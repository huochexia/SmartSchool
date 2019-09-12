package com.goldenstraw.restaurant.goodsmanager.repositories

import com.goldenstraw.restaurant.goodsmanager.http.entity.newObject
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.network.filterStauts
import com.owner.basemodule.room.dao.CategoryAndAllGoods
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

/**
 * 商品数据源，需要处理来自本地和远程的数据，所以它要继承同时拥有两个数据源的类
 */
class GoodsRepository(
    private val remote: IRemoteGoodsDataSource,
    private val local: ILocalGoodsDataSource
) : BaseRepositoryBoth<IRemoteGoodsDataSource, ILocalGoodsDataSource>(remote, local) {

    /**
     * 增加
     */
    //1、 增加商品到远程数据库,成功后取出objectId，赋值给Goods对象，然后保存本地库

    fun addGoods(goods: Goods): Observable<newObject> {
        return filterStauts(remote.addGoods(goods))
            .doAfterNext {
                goods.categoryCode = it.objectId
                local.addGoods(goods)  //这个地方出现异常会怎样？抛出吗？
            }

    }


    //2、增加商品类别到远程数据库，成功后得到新类别的objectId,赋与本地的新类别，然后保存本地库

    fun addGoodsCategory(category: GoodsCategory): Observable<newObject> {
        return filterStauts(remote.addCategory(category))
            .doAfterNext {
                category.code = it.objectId
                local.addCategory(category)
            }
    }

    /**
     * 更新
     */
    //1、更新远程数据后，更新本地数据
    fun updateGoods(goods: Goods): Completable {
        return remote.updateGoods(goods)
            .doOnComplete {
                local.updateGoods(goods)
            }
    }

    //2、更新类别
    fun updateCategory(category: GoodsCategory): Completable {
        return remote.updateCategory(category)
            .doOnComplete {
                local.updateGoodsCategory(category)
            }
    }

    /*
      按类别查询商品,此处只从本地数据源中获取。
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