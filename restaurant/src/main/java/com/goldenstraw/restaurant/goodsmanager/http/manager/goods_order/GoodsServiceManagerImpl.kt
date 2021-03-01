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
import io.reactivex.Observable

/**
 * 网络访问远程数据库的具体实现类
 */
class GoodsServiceManagerImpl(
    private val serverApi: GoodsApi
) : IGoodsServiceManager {
    /**
     * 获取,分离有用数据。
     */
    override suspend fun getAllOfCategory(): ObjectList<GoodsCategory> {
        return serverApi.getAllCategory()
    }

    override suspend fun getGoodsOfCategory(category: GoodsCategory): ObjectList<Goods> {
        val condition = """{"categoryCode":"${category.objectId}"}"""
        return serverApi.getGoodsList(condition)
    }

    /**
     * 删除
     */

    override suspend fun deleteCategory(category: GoodsCategory) {
        serverApi.deleteCategory(category.objectId)
    }

    override suspend fun deleteGoods(goods: Goods) {
        serverApi.deleteGoods(goods.objectId)
    }

    override suspend fun getCookBookOfDailyMeal(where: String): ObjectList<DailyMeal> {
        return serverApi.getCookBookOfDailyMeal(where)
    }

    /**
     * 增加
     */
    override suspend fun addGoodsToRemote(goods: NewGoods): CreateObject {
        return serverApi.createGoods(goods)
    }

    override suspend fun addCategoryToRemote(category: NewCategory): CreateObject {
        return serverApi.createGoodsCategory(category)
    }


    /**
     * 更新
     */
    override suspend fun updateGoodsToRemote(goods: NewGoods, objectId: String) {
        serverApi.updateGoods(goods, objectId)
    }

    override suspend fun updateCategoryToRemote(category: NewCategory, objectId: String) {
        serverApi.updateCategory(category, objectId)
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


}