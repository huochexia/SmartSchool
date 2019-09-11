package com.goldenstraw.restaurant.goodsmanager.http.manager

import com.goldenstraw.restaurant.goodsmanager.http.entity.createObject
import com.goldenstraw.restaurant.goodsmanager.http.service.GoodsApi
import com.owner.basemodule.network.filterStauts
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Observable

/**
 * 网络访问远程数据库的具体实现类
 */
class GoodsServiceManagerImpl(
    private val serverApi: GoodsApi
) : IGoodsServiceManager {

    override fun addGoods(goods: Goods): Observable<createObject> {
        return filterStauts(serverApi.createGoods(goods))
    }

    override fun addCategory(category: GoodsCategory): Observable<createObject> {
        return filterStauts(serverApi.createGoodsCategory(category))
    }
}