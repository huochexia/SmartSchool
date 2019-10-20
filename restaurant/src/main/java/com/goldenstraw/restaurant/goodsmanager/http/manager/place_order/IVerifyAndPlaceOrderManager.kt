package com.goldenstraw.restaurant.goodsmanager.http.manager.place_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import io.reactivex.Observable

interface IVerifyAndPlaceOrderManager {

    /**
     * 获取某个日期的订单
     */
    fun getAllOrderOfDate(date: String): Observable<MutableList<OrderItem>>
}