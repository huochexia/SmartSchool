package com.goldenstraw.restaurant.goodsmanager.http.manager

import com.goldenstraw.restaurant.goodsmanager.http.entity.newObject
import com.goldenstraw.restaurant.goodsmanager.http.entity.objectList
import com.goldenstraw.restaurant.goodsmanager.http.service.GoodsApi
import com.owner.basemodule.network.HttpResult
import com.owner.basemodule.network.filterStauts
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * 网络访问远程数据库的具体实现类
 */
class GoodsServiceManagerImpl(
    private val serverApi: GoodsApi
) : IGoodsServiceManager {
    override fun getCategory(): Observable<objectList<GoodsCategory>> {
        return filterStauts(serverApi.getAllCategory())
    }

    override fun getGoodsOfCategory(category: GoodsCategory): Observable<objectList<Goods>> {
        val map = HashMap<String, String>()
        map.put("categoryCode", category.code)
        return filterStauts(serverApi.getGoodsList(map))
    }

    /**
     * 增加
     */
    override fun addGoods(goods: Goods): Single<HttpResult<newObject>> {
        return serverApi.createGoods(goods)
    }

    override fun addCategory(category: GoodsCategory): Single<HttpResult<newObject>> {
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