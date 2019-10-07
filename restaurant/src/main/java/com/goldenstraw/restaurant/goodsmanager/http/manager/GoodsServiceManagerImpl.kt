package com.goldenstraw.restaurant.goodsmanager.http.manager

import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.objectList
import com.goldenstraw.restaurant.goodsmanager.http.service.GoodsApi
import com.owner.basemodule.network.HttpResult
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

    /**
     * 删除
     */

    override fun deleteCategory(category: GoodsCategory): Completable {
        return serverApi.deleteCategory(category.code)
    }

    override fun deleteGoods(goods: Goods): Completable {
        return serverApi.deleteGoods(goods.goodsCode)
    }

    /**
     * 获取
     */
    override fun getCategory(): Observable<HttpResult<objectList<GoodsCategory>>> {
        return serverApi.getAllCategory()
    }

    override fun getGoodsOfCategory(category: GoodsCategory): Observable<HttpResult<objectList<Goods>>> {

        val condition = """{”categoryCode":"${category.code}"}"""
        return serverApi.getGoodsList(condition)
    }

    /**
     * 增加
     */
    override fun addGoods(goods: Goods): Single<CreateObject> {
        return serverApi.createGoods(goods)
    }

    override fun addCategory(category: GoodsCategory): Single<CreateObject> {
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