package com.goldenstraw.restaurant.goodsmanager.ui.recheck.util

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * 远程访问数据
 */
interface IRecheckOrderDataSource : IRemoteDataSource {
    /*
       获取某个日期的商品订单
        */
    fun getAllOrderOfDate(condition: String): Observable<MutableList<OrderItem>>


    fun getAllSupplier(): Observable<MutableList<User>>

    fun updateOrderItemQuantity(newQuantity: ObjectReCheck, objectId: String): Completable


    fun batchRecheckQuantity(orders: BatchOrdersRequest<BatchRecheckObject>): Completable

}

class RecheckOrderDataSourceImpl(
    private val manager: IRecheckOrderManager
) : IRecheckOrderDataSource {
    override fun updateOrderItemQuantity(
        newQuantity: ObjectReCheck,
        objectId: String
    ): Completable {
        return manager.RecheckOrderItem(newQuantity, objectId)
    }

    override fun batchRecheckQuantity(orders: BatchOrdersRequest<BatchRecheckObject>): Completable {
        return manager.batchReCheckQuantityOfOrder(orders)
    }

    override fun getAllOrderOfDate(condition: String): Observable<MutableList<OrderItem>> {
        return manager.getAllOrderOfDate(condition)
    }

    override fun getAllSupplier(): Observable<MutableList<User>> {
        return manager.getAllSupplier()
    }

}