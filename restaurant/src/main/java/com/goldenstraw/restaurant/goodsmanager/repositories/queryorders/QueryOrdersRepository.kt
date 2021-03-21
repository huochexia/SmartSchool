package com.goldenstraw.restaurant.goodsmanager.repositories.queryorders

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.base.repository.BaseRepositoryRemote
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User

class QueryOrdersRepository(
    private val remote: IRemoteQueryOrdersDataSource
) : BaseRepositoryRemote<IRemoteQueryOrdersDataSource>(remote) {

    /**
     * 获取所有供应商
     */
    suspend fun getAllSupplier(): ObjectList<User> {
        return remote.getAllSupplier()
    }

    /**
     *按条件获取供应商订单
     */
    suspend fun getAllOfOrders(where: String): ObjectList<OrderItem> {
        return remote.getAllOfOrders(where)
    }

    /**
     * 使用协程的方式，按条件获取订单.
     */
    suspend fun getOrdersList(where: String): ObjectList<OrderItem> =
        remote.getOrdersList(where)


    /**
     * 修改订单的单价
     */
    suspend fun updateUnitPriceOfOrders(newPrice: ObjectUnitPrice, objectId: String): UpdateObject =
        remote.updateUnitPrice(newPrice, objectId)


    /**
     * 修改订单的供应商
     */
    suspend fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): UpdateObject {
        return remote.updateOrderOfSupplier(newOrder, objectId)
    }

    /**
     * 获取商品信息
     */
    suspend fun getGoodsOfCategory(condition: String): ObjectList<Goods> =
        remote.getGoodsOfCategory(condition)



    /**
     * 求和
     */
    suspend fun getTotalOfSupplier(condition: String): ObjectList<SumResult> {
        return remote.getTotalOfSupplier(condition)
    }

    /**
     * 分组求和
     */
    suspend fun getTotalGroupByName(condition: String): ObjectList<SumByGroup> {
        return remote.getTotalGroupByName(condition)
    }

    /**
     * 更新数据库商品新单价
     */
    suspend fun updateNewPrice(newPrice: NewPrice, objectId: String) =

        remote.updateNewPrice(newPrice, objectId)


    /**
     * 获取每日菜单
     */
    suspend fun getDailyMeals(where: String): ObjectList<DailyMeal> {
        return remote.getDailyMeals(where)
    }

    /**
     * 获取每日菜单对应的菜谱
     */
    suspend fun getCookBook(objectId: String): RemoteCookBook {

        return remote.getCookBook(objectId)

    }

    /**
     * 获取材料对应的菜谱
     */
    suspend fun getGoods(objectId: String): Goods {
        return remote.getGoods(objectId)
    }

    suspend fun deleteOrderItem(objectId: String) =
        remote.deleteOrderItem(objectId)


    suspend fun updateOrderItem(
        newOrderItem: ObjectQuantityAndNote,
        objectId: String
    ): UpdateObject {
        return remote.updateOrderItem(newOrderItem, objectId)
    }
}