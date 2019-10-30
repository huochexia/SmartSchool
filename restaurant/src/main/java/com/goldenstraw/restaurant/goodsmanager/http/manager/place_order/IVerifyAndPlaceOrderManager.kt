package com.goldenstraw.restaurant.goodsmanager.http.manager.place_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

interface IVerifyAndPlaceOrderManager {

    /**
     * 获取某个日期的拟购单
     */
    fun getAllOrderOfDate(condition: String): Observable<MutableList<OrderItem>>

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

    /**
     * 确定实际数量
     */
    fun setCheckQuantity(newQuantity: ObjectCheckGoods, objectId: String): Completable

    /**
     * 确定实际数量
     */
    fun batchCheckQuantityOfOrders(orders: BatchOrdersRequest<ObjectCheckGoods>): Completable
    /**
     * 确定记帐
     */
    fun commitRecordState(orders:BatchOrdersRequest<ObjectState>):Completable
}