package com.goldenstraw.restaurant.goodsmanager.http.manager.place_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectQuantity
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

interface IVerifyAndPlaceOrderManager {

    /**
     * 获取某个日期的拟购单
     */
    fun getAllOrderOfDate(date: String): Observable<MutableList<OrderItem>>

    /**
     * 批量发送订单给供应商
     */
    fun sendOrdersToSupplier(orders: BatchOrdersRequest<ObjectSupplier>): Completable

    /**
     *  获取所有供应商
     */

    fun getAllSupplier(): Observable<MutableList<User>>

    /**
     * 修改订单数量
     */
    fun updateOrderItemQuantity(newQuantity: ObjectQuantity, objectId: String): Completable

}