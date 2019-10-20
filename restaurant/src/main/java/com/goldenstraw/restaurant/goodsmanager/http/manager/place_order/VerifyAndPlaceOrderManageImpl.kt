package com.goldenstraw.restaurant.goodsmanager.http.manager.place_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewOrderItem
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.http.service.VerifyAndPlaceOrderApi
import com.owner.basemodule.network.ApiException
import io.reactivex.Completable
import io.reactivex.Observable

class VerifyAndPlaceOrderManageImpl(

    private val service: VerifyAndPlaceOrderApi

) : IVerifyAndPlaceOrderManager {

    override fun sendOrdersToSupplier(orders: BatchOrdersRequest<ObjectSupplier>): Completable {
        return service.batchAddOrderToSupplier(orders)
    }

    /**
     * 获取某个日期的全部商品订单
     */
    override fun getAllOrderOfDate(date: String): Observable<MutableList<OrderItem>> {
        val condition = """{"orderDate":"$date"}"""
        return service.getAllOrderOfDate(condition).map {
            if (!it.isSuccess()) {
                throw ApiException(it.code)
            }
            it.results
        }
    }

}