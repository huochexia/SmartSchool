package com.goldenstraw.restaurant.goodsmanager.viewmodel

import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.owner.basemodule.base.viewmodel.BaseViewModel
import io.reactivex.Observable

class VerifyAndPlaceOrderViewModel(
    private val repository: VerifyAndPlaceOrderRepository
) : BaseViewModel() {

    /**
     * 获取订单
     */
    fun getAllOrderOfDate(date: String): Observable<MutableList<OrderItem>> {
        return repository.getAllOrderOfDate(date)
    }

}