package com.goldenstraw.restaurant.goodsmanager.repositories.goods_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCategory
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.goldenstraw.restaurant.goodsmanager.http.manager.goods_order.IGoodsServiceManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.User
import io.reactivex.Observable

/**
 * 对商品的远程数据操作接口
 */
interface IRemoteGoodsDataSource : IRemoteDataSource {
    /**
     * 增加
     */
    suspend fun addGoodsToRemote(goods: NewGoods): CreateObject

    suspend fun addCategoryToRemote(category: NewCategory): CreateObject

    /**
     * 更新
     */
    suspend fun updateGoodsToRemote(goods: NewGoods, objectId: String): UpdateObject

    suspend fun updateCategoryToRemote(category: NewCategory, objectId: String): UpdateObject

    /**
     * 获取
     */
    suspend fun getAllOfCategory(): ObjectList<GoodsCategory>

    suspend fun getGoodsOfCategory(category: GoodsCategory): ObjectList<Goods>

    suspend fun getAllOfGoods(skip:Int):ObjectList<Goods>

    fun getAllSupplier(): Observable<MutableList<User>>

    /**
     * 删除
     */
    suspend fun deleteGoods(goods: Goods): DeleteObject

    suspend fun deleteCategory(goodsCategory: GoodsCategory): DeleteObject

    /**
     * 获取某一天菜单
     */
    suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal>
}

/**
 * 远程数据操作实现类
 */
class RemoteGoodsDataSourceImpl(
    private val service: IGoodsServiceManager
) : IRemoteGoodsDataSource {
    /**
     * 获取
     */
    override suspend fun getAllOfCategory(): ObjectList<GoodsCategory> {
        return service.getAllOfCategory()
    }

    override suspend fun getGoodsOfCategory(category: GoodsCategory): ObjectList<Goods> {
        return service.getGoodsOfCategory(category)
    }

    override suspend fun getAllOfGoods(skip: Int): ObjectList<Goods> {
        return service.getAllOfGoods(skip)
    }

    /**
     * 删除
     */
    override suspend fun deleteGoods(goods: Goods) =
        service.deleteGoods(goods)


    override suspend fun deleteCategory(goodsCategory: GoodsCategory) =
        service.deleteCategory(goodsCategory)


    /**
     * 增加
     */
    override suspend fun addGoodsToRemote(goods: NewGoods): CreateObject {
        return service.addGoodsToRemote(goods)
    }

    override suspend fun addCategoryToRemote(category: NewCategory): CreateObject {
        return service.addCategoryToRemote(category)
    }

    /**
     * 更新
     */
    override suspend fun updateGoodsToRemote(goods: NewGoods, objectId: String) =
        service.updateGoodsToRemote(goods, objectId)


    override suspend fun updateCategoryToRemote(category: NewCategory, objectId: String) =
        service.updateCategoryToRemote(category, objectId)


    override suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal> {
        return service.getCookBookOfDailyMeal(where)
    }

    override fun getAllSupplier(): Observable<MutableList<User>> {
        return service.getAllSupplier()
    }

}