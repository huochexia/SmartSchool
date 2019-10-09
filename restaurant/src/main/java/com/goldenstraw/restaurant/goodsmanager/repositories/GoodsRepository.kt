package com.goldenstraw.restaurant.goodsmanager.repositories

import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.network.ApiException
import com.owner.basemodule.room.dao.CategoryAndAllGoods
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

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

    fun addGoods(goods: Goods): Observable<Goods> {
        return remote.addGoods(goods).toObservable()
            .map {
                if (!it.isSuccess()) {
                    throw ApiException(it.code)
                }
                goods.goodsCode = it.objectId!!
                local.addGoods(goods).subscribeOn(Schedulers.io())
                    .subscribe()
                goods
            }

    }


    //2、增加商品类别到远程数据库，传入类别名称，成功后得到新类别的objectId,使用objectId,创建新类别，然后保存本地库

    fun addGoodsCategory(category: GoodsCategory): Observable<GoodsCategory> {
        return remote.addCategory(category).toObservable()
            .map {
                if (!it.isSuccess()) {
                    throw ApiException(it.code)
                }
                category.code = it.objectId!!
                local.addCategory(category).subscribeOn(Schedulers.io())
                    .subscribe()
                category
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
      获取所有商品类别, 这里应该考虑从远程获取，然后保存于本地，最后从本地获得结果。
      先暂时实现从本地直接获取
     */
    fun getAllCategory(): Observable<MutableList<GoodsCategory>> {
        return local.getAllCategory()
    }

    /*
      按类别查询商品,此处只从本地数据源中获取。
     */
    fun queryGoods(category: GoodsCategory): Observable<MutableList<Goods>> {

        return local.getGoodsFromCategory(category)

    }

    /*
      获得所有类别及其所拥入商品
     */
    fun getCategory(): Observable<MutableList<CategoryAndAllGoods>> {
        return local.getCategoryAllGoods()
    }
}