package com.goldenstraw.restaurant.goodsmanager.repositories.queryorders

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.http.manager.query_orders.IQueryOrdersManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

interface IRemoteQueryOrdersDataSource : IRemoteDataSource {
    fun getAllSupplier(): Observable<MutableList<User>>

    fun getAllOfOrders(where: String): Observable<MutableList<OrderItem>>

    fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable

    fun getGoodsOfCategory(condition: String): Observable<MutableList<Goods>>

    fun getTotalOfSupplier(condition: String):Observable<MutableList<SumResult>>

    fun getTotalGroupByName(condition: String):Observable<MutableList<SumByGroup>>

    fun updateNewPrice(newPrice: NewPrice, objectId: String): Completable

    fun getCookBookOfDailyMeal(where: String):Observable<ObjectList<DailyMeal>>

    suspend fun deleteOrderItem(objectId: String)

    suspend fun updateOrderItem(newOrder:ObjectQuantityAndNote,objectId: String)
}

class RemoteQueryOrdersDataSourceImpl(
    private val manager: IQueryOrdersManager
) : IRemoteQueryOrdersDataSource {
    /**
     *
     */
    override fun getAllOfOrders(
        where: String
    ): Observable<MutableList<OrderItem>> {
        return manager.getAllOfOrders(where)
    }

    override fun getAllSupplier(): Observable<MutableList<User>> {
        return manager.getAllSupplier()
    }

    override fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable {
        return manager.updateOrderOfSupplier(newOrder, objectId)
    }

    override fun getGoodsOfCategory(condition: String): Observable<MutableList<Goods>> {
        return manager.getGoodsOfCategory(condition)
    }

    override fun getTotalOfSupplier(condition: String): Observable<MutableList<SumResult>> {
        return manager.getTotalOfSupplier(condition)
    }

    override fun getTotalGroupByName(condition: String): Observable<MutableList<SumByGroup>> {
        return manager.getTotalGroupByName(condition)
    }

    override fun updateNewPrice(newPrice: NewPrice, objectId: String): Completable {
        return manager.updateNewPriceOfGoods(newPrice, objectId)
    }

    override fun getCookBookOfDailyMeal(where: String): Observable<ObjectList<DailyMeal>> {
        return manager.getCookBookOfDailyMeal(where)
    }

    override suspend fun deleteOrderItem(objectId: String) {
        manager.deleteOrderItem(objectId)
    }

    override suspend fun updateOrderItem(newOrder: ObjectQuantityAndNote, objectId: String) {
        manager.updateOrderItemQuantityAndNote(newOrder,objectId)
    }
}