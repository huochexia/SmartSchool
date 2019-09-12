package com.goldenstraw.restaurant.goodsmanager.http.manager

import com.goldenstraw.restaurant.goodsmanager.http.entity.newObject
import com.goldenstraw.restaurant.goodsmanager.http.service.GoodsApi
import com.owner.basemodule.network.HttpResult
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * 网络访问远程数据库的具体实现类
 */
class GoodsServiceManagerImpl(
    private val serverApi: GoodsApi
) : IGoodsServiceManager {
    /**
     * 增加
     */
    override fun addGoods(goods: Goods): Observable<HttpResult<newObject>> {
        return serverApi.createGoods(goods)
    }

    override fun addCategory(category: GoodsCategory): Observable<HttpResult<newObject>> {
        return serverApi.createGoodsCategory(category)
    }

    /**
     * 更新
     */
    override fun updateGoods(goods: Goods): Completable {
        return serverApi.updateGoods(goods, goods.goodsCode)
    }

    override fun updateCategory(category: GoodsCategory): Completable {
        return serverApi.updateCategory(category, category.code)
    }
}