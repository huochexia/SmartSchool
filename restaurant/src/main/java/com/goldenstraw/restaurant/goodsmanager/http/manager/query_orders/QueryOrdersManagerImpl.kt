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
     * 'where={"$and":[{"supplier":XXXXX},{"date":XXXXX}]}'
     *  条件查询：查询小于某日期订单
     *  where1={"orderDate":{"$lt":XXXX}}
     *  表达式：
     *    val where1 = "{\"orderDate\":{\"\$lt\":\"$date\"}}"
     */
    override fun getOrderOfSupplier(
        where:String
    ): Observable<MutableList<OrderItem>> {

        return service.getOrdersOfSupplier(where).map {
            if (!it.isSuccess()) {
                throw ApiException(it.code)
            }
            it.results
        }

    }

    /**
     * 修改订单的供应商为空
     */
    override fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable {
        return service.updateOrderOfSupplier(newOrder, objectId)
    }
}