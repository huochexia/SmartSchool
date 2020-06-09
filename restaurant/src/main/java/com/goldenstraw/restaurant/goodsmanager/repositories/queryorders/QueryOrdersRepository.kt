package com.goldenstraw.restaurant.goodsmanager.repositories.queryorders

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.base.repository.BaseRepositoryRemote
import com.owner.basemodule.network.ObjectList
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
     *按日期获取供应商订单
     */
    fun getAllOfOrders(where: String): Observable<MutableList<OrderItem>> {
        return remote.getAllOfOrders(where)
    }

    /**
     * 修改订单的供应商
     */
    fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable {
        return remote.updateOrderOfSupplier(newOrder, objectId)
    }

    /**
     * 获取商品信息
     */
    fun getGoodsOfCategory(condition: String): Observable<MutableList<Goods>> {
        return remote.getGoodsOfCategory(condition)
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
    fun updateNewPrice(newPrice: NewPrice, objectId: String): Completable {

        return remote.updateNewPrice(newPrice, objectId)

    }

    /**
     * 获取每日菜单当中的菜谱
     */
    fun getCookBookOfDailyMeal(where: String): Observable<ObjectList<DailyMeal>> {

        return remote.getCookBookOfDailyMeal(where)
    }

    suspend fun deleteOrderItem(objectId: String) {
        remote.deleteOrderItem(objectId)
    }

    suspend fun updateOrderItem(newOrderItem: ObjectQuantityAndNote, objectId: String) {
        remote.updateOrderItem(newOrderItem, objectId)
    }
}