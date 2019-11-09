package com.goldenstraw.restaurant.goodsmanager.ui.recheck.util

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.owner.basemodule.network.ApiException
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

class RecheckOrderManageImpl(

    private val service: RecheckOrderApi

) : IRecheckOrderManager {
    override fun RecheckOrderItem(newOrder: ObjectReCheck, objectId: String): Completable {
        return service.RecheckOrderItem(newOrder, objectId)
    }

    override fun batchReCheckQuantityOfOrder(orders: BatchOrdersRequest<BatchRecheckObject>): Completable {
        return service.batchReCheckQuantityOfOrder(orders)
    }

    override fun getAllOrderOfDate(
        condition: String

    ): Observable<MutableList<OrderItem>> {

        return service.getAllOrderOfDate(condition)
            .map {
                if (!it.isSuccess()) {
                    throw ApiException(it.code)
                }
                it.results
            }
    }

    override fun getAllSupplier(): Observable<MutableList<User>> {
        val where = """{"role":"供应商"}"""
        return service.getAllSupplier(where).map {
            if (!it.isSuccess()) {
                throw ApiException(it.code)
            }
            it.results
        }
    }

    override fun getTotalOfSuppliers(condition: String): Observable<MutableList<SupplierOfTotal>> {

        return service.getTotalOfSuppliers(condition = condition)
            .map {
                if (!it.isSuccess()) {
                    throw ApiException(it.code)
                }
                it.results
            }
    }

}