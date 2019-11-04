package com.goldenstraw.restaurant.goodsmanager.ui.recheck.util

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

interface IRecheckOrderManager {

    /**
     * 修改订单
     */

    fun RecheckOrderItem(newOrder: ObjectReCheck, objectId: String): Completable


    /**
     * 批量修改供应商实际数量
     */

    fun batchReCheckQuantityOfOrder(orders: BatchOrdersRequest<BatchRecheckObject>): Completable
    /**
     *  获取所有供应商
     */

    fun getAllSupplier(): Observable<MutableList<User>>
    /**
     * 获取某个日期的拟购单
     */
    fun getAllOrderOfDate(condition: String): Observable<MutableList<OrderItem>>

}