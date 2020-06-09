package com.goldenstraw.restaurant.goodsmanager.http.manager.goods_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCategory
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.goldenstraw.restaurant.goodsmanager.http.service.GoodsApi
import com.owner.basemodule.network.ApiException
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.User
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
        return serverApi.deleteCategory(category.objectId)
    }

    override fun deleteGoods(goods: Goods): Completable {
        return serverApi.deleteGoods(goods.objectId)
    }

    override fun getCookBookOfDailyMeal(where: String): Observable<ObjectList<DailyMeal>> {
        return serverApi.getCookBookOfDailyMeal(where)
    }

    /**
     * 获取,分离有用数据。
     */
    override fun getCategory(): Observable<MutableList<GoodsCategory>> {
        return serverApi.getAllCategory().map {
            if (!it.isSuccess()) {
                throw ApiException(it.code)
            }
            it.results
        }
    }

    override fun getGoodsOfCategory(category: GoodsCategory): Observable<MutableList<Goods>> {

        val condition = """{"categoryCode":"${category.objectId}"}"""
        return serverApi.getGoodsList(condition).map {
            if (!it.isSuccess()) {
                throw ApiException(it.code)
            }
            it.results
        }

    }

    override fun getAllGoods(): Observable<MutableList<Goods>> {
        return serverApi.getAllGoods().map {
            if (!it.isSuccess()) {
                throw ApiException(it.code)
            }
            it.results
        }
    }

    override fun getAllSupplier(): Observable<MutableList<User>> {
        val where = """{"role":"供应商"}"""
        return serverApi.getAllSupplier(where).map {
            if (!it.isSuccess()) {
                throw ApiException(it.code)
            }
            it.results
        }
    }

    /**
     * 增加
     */
    override fun addGoods(goods: NewGoods): Single<CreateObject> {
        return serverApi.createGoods(goods)
    }

    override fun addCategory(category: NewCategory): Single<CreateObject> {
        return serverApi.createGoodsCategory(category)
    }


    /**
     * 更新
     */
    override fun updateGoods(goods: NewGoods, objectId: String): Completable {
        return serverApi.updateGoods(goods, objectId)
    }

    override fun updateCategory(category: NewCategory, objectId: String): Completable {
        return serverApi.updateCategory(category, objectId)
    }
}