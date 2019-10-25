package com.goldenstraw.restaurant.goodsmanager.http.manager.query_orders

import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.http.service.QueryOrdersApi
import com.owner.basemodule.network.ApiException
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

class QueryOrdersManagerImpl(
    private val service: QueryOrdersApi
) : IQueryOrdersManager {
    /**
     * 单一条件查询
     * 'where={"score":{"$gte":1000,"$lte":3000}}'
     */
    override fun getAllSupplier(): Observable<MutableList<User>> {
        val where = """{"role":"供应商"}"""
        return service.getAllSupplier(where).map {
            if (!it.isSuccess()) {
                throw ApiException(it.code)
            }
            it.results
        }
    }

    /**
     * 复合查询，供应商和日期
     * 'where={"$or":[{"wins":{"$gt":150}},{"wins":{"$lt":5}}]}'
     *  因为写不成标准的语句，采用先得到某日期的，然后过滤出对应供应商的。
     */
    override fun getOrderOfSupplier(
        supplier: String,
        date: String
    ): Observable<MutableList<OrderItem>> {
        val where = """{"orderDate":"$date"}"""
        return service.getOrdersOfSupplier(where).map {
            if (!it.isSuccess()) {
                throw ApiException(it.code)
            }
            it.results
        }.flatMap {
            Observable.fromIterable(it)
        }.filter {
            it.supplier == supplier
        }.buffer(100)

    }

    /**
     * 修改订单的供应商为空
     */
    override fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable {
        return service.updateOrderOfSupplier(newOrder, objectId)
    }
}