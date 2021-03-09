package com.goldenstraw.restaurant.goodsmanager.repositories.queryorders

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.base.repository.BaseRepositoryRemote
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

class QueryOrdersRepository(
    private val remote: IRemoteQueryOrdersDataSource
) : BaseRepositoryRemote<IRemoteQueryOrdersDataSource>(remote) {

    /**
     * 获取所有供应商
     */
    fun getAllSupplier(): Observable<MutableList<User>> {
        return remote.getAllSupplier()
    }

    /**
     *按条件获取供应商订单
     */
    fun getAllOfOrders(where: String): Observable<MutableList<OrderItem>> {
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
    fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable {
        return remote.updateOrderOfSupplier(newOrder, objectId)
    }

    /**
     * 获取商品信息
     */
    suspend fun getGoodsOfCategory(condition: String): ObjectList<Goods> =
        remote.getGoodsOfCategory(condition)


    fun getGoodsFromObjectId(id: String): Observable<Goods> {
        return remote.getGoodsFromObjectId(id)
    }

    /**
     * 求和
     */
    fun getTotalOfSupplier(condition: String): Observable<MutableList<SumResult>> {
        return remote.getTotalOfSupplier(condition)
    }

    /**
     * 分组求和
     */
    fun getTotalGroupByName(condition: String): Observable<MutableList<SumByGroup>> {
        return remote.getTotalGroupByName(condition)
    }

    /**
     * 更新数据库商品新单价
     */
    suspend fun updateNewPrice(newPrice: NewPrice, objectId: String) =

        remote.updateNewPrice(newPrice, objectId)


    /**
     * 获取每日菜单当中的菜谱
     */
    fun getCookBookOfDailyMeal(where: String): Observable<ObjectList<DailyMeal>> {

        return remote.getCookBookOfDailyMeal(where)
    }

    suspend fun deleteOrderItem(objectId: String) =
        remote.deleteOrderItem(objectId)


    suspend fun updateOrderItem(newOrderItem: ObjectQuantityAndNote, objectId: String):UpdateObject {
        return remote.updateOrderItem(newOrderItem, objectId)
    }
}