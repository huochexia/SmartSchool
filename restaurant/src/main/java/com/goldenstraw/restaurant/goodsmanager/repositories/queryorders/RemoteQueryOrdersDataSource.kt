package com.goldenstraw.restaurant.goodsmanager.repositories.queryorders

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.http.manager.query_orders.IQueryOrdersManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

interface IRemoteQueryOrdersDataSource : IRemoteDataSource {
    fun getAllSupplier(): Observable<MutableList<User>>

    fun getOrdersOfSupplier(where: String): Observable<MutableList<OrderItem>>

    fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable

    fun getGoodsOfCategory(condition: String): Observable<MutableList<Goods>>

    fun getTotalOfSupplier(condition: String):Observable<MutableList<SumResult>>

    fun getTotalGroupByName(condition: String):Observable<MutableList<SumByGroup>>

    fun updateNewPrice(newPrice: NewPrice, objectId: String): Completable
}

class RemoteQueryOrdersDataSourceImpl(
    private val manager: IQueryOrdersManager
) : IRemoteQueryOrdersDataSource {
    /**
     *
     */
    override fun getOrdersOfSupplier(
        where: String
    ): Observable<MutableList<OrderItem>> {
        return manager.getOrderOfSupplier(where)
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
}