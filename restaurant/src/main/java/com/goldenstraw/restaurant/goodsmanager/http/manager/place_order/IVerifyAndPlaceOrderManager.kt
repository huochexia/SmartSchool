package com.goldenstraw.restaurant.goodsmanager.http.manager.place_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable

interface IVerifyAndPlaceOrderManager {

    /**
     * 获取某个日期的拟购单
     */
    suspend fun getOrdersOfDate(condition: String): ObjectList<OrderItem>

    /**
     * 批量发送订单给供应商
     */
    fun sendOrdersToSupplier(orders: BatchOrdersRequest<ObjectSupplier>): Completable

    /**
     *  获取所有供应商
     */

    suspend fun getSupplier(where:String): ObjectList<User>

    /**
     * 删除订单
     */
    suspend fun deleteOrderItem(objectId: String): DeleteObject
    /**
     * 修改订单数量
     */
    suspend fun updateOrderItemQuantity(newQuantity: ObjectQuantityAndNote, objectId: String):UpdateObject

    /**
     * 确定实际数量
     */
    suspend fun setCheckQuantity(newQuantity: ObjectCheckGoods, objectId: String):UpdateObject

    /**
     * 确定实际数量
     */
    fun batchCheckQuantityOfOrders(orders: BatchOrdersRequest<ObjectState>): Completable
    /**
     * 确定记帐State
     */
    fun commitRecordState(orders:BatchOrdersRequest<ObjectState>):Completable

}