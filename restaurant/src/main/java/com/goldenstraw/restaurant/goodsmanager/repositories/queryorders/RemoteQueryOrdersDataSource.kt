package com.goldenstraw.restaurant.goodsmanager.repositories.queryorders

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.http.manager.query_orders.IQueryOrdersManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

interface IRemoteQueryOrdersDataSource : IRemoteDataSource {

    suspend fun getAllSupplier(): ObjectList<User>

    suspend fun getAllOfOrders(where: String): ObjectList<OrderItem>

    suspend fun getOrdersList(where: String): ObjectList<OrderItem>

    suspend fun updateUnitPrice(newPrice: ObjectUnitPrice, objectId: String): UpdateObject

    fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable

    suspend fun getGoodsOfCategory(condition: String): ObjectList<Goods>

    fun getGoodsFromObjectId(id: String): Observable<Goods>

    suspend fun getTotalOfSupplier(condition: String): ObjectList<SumResult>

    fun getTotalGroupByName(condition: String): Observable<MutableList<SumByGroup>>

    suspend fun updateNewPrice(newPrice: NewPrice, objectId: String):UpdateObject

    fun getCookBookOfDailyMeal(where: String): Observable<ObjectList<DailyMeal>>

    suspend fun deleteOrderItem(objectId: String):DeleteObject

    suspend fun updateOrderItem(newOrder:ObjectQuantityAndNote,objectId: String):UpdateObject
}

class RemoteQueryOrdersDataSourceImpl(
    private val manager: IQueryOrdersManager
) : IRemoteQueryOrdersDataSource {
    /**
     *
     */
    override suspend fun getAllOfOrders(
        where: String
    ): ObjectList<OrderItem> {
        return manager.getAllOfOrders(where)
    }

    override suspend fun getOrdersList(where: String): ObjectList<OrderItem> {
        return manager.getOrdersList(where)
    }

    override suspend fun updateUnitPrice(newPrice: ObjectUnitPrice, objectId: String) =
        manager.updateUnitPrice(newPrice, objectId)


    override suspend fun getAllSupplier(): ObjectList<User> {
        return manager.getAllSupplier()
    }

    override fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable {
        return manager.updateOrderOfSupplier(newOrder, objectId)
    }

    override suspend fun getGoodsOfCategory(condition: String) =
        manager.getGoodsOfCategory(condition)


    override fun getGoodsFromObjectId(id: String): Observable<Goods> {
        return manager.getGoodsFromObjectId(id)
    }

    override suspend fun getTotalOfSupplier(condition: String): ObjectList<SumResult> {
        return manager.getTotalOfSupplier(condition)
    }

    override fun getTotalGroupByName(condition: String): Observable<MutableList<SumByGroup>> {
        return manager.getTotalGroupByName(condition)
    }

    override suspend fun updateNewPrice(newPrice: NewPrice, objectId: String)=
         manager.updateNewPriceOfGoods(newPrice, objectId)


    override fun getCookBookOfDailyMeal(where: String): Observable<ObjectList<DailyMeal>> {
        return manager.getCookBookOfDailyMeal(where)
    }

    override suspend fun deleteOrderItem(objectId: String) =
        manager.deleteOrderItem(objectId)


    override suspend fun updateOrderItem(newOrder: ObjectQuantityAndNote, objectId: String) =
        manager.updateOrderItemQuantityAndNote(newOrder,objectId)

}