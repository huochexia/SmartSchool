package com.goldenstraw.restaurant.goodsmanager.repositories.queryorders

import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
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
}