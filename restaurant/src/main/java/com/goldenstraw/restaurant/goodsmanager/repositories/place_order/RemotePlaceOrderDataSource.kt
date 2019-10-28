package com.goldenstraw.restaurant.goodsmanager.repositories.place_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.http.manager.place_order.IVerifyAndPlaceOrderManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * 远程访问数据
 */
interface IRemotePlaceOrderDataSource : IRemoteDataSource {

    /*
    获取某个日期的商品订单
     */
    fun getAllOrderOfDate(date: String,state:Int): Observable<MutableList<OrderItem>>

    /*
    将订单发送给供应商
     */
    fun sendOrdersToSupplier(orders: BatchOrdersRequest<ObjectSupplier>): Completable

    fun getAllSupplier(): Observable<MutableList<User>>

    fun updateOrderItemQuantity(newQuantity: ObjectQuantity, objectId: String): Completable

    fun checkQuantityOfOrders(orders: BatchOrdersRequest<ObjectCheckGoods>): Completable
}

class RemotePlaceOrderDataSourceImpl(
    private val manager: IVerifyAndPlaceOrderManager
) : IRemotePlaceOrderDataSource {

    override fun sendOrdersToSupplier(orders: BatchOrdersRequest<ObjectSupplier>): Completable {
        return manager.sendOrdersToSupplier(orders)
    }

    override fun getAllOrderOfDate(date: String,state:Int): Observable<MutableList<OrderItem>> {
        return manager.getAllOrderOfDate(date,state)
    }

    override fun getAllSupplier(): Observable<MutableList<User>> {
        return manager.getAllSupplier()
    }

    override fun updateOrderItemQuantity(
        newQuantity: ObjectQuantity,
        objectId: String
    ): Completable {
        return manager.updateOrderItemQuantity(newQuantity, objectId)
    }

    override fun checkQuantityOfOrders(orders: BatchOrdersRequest<ObjectCheckGoods>): Completable {
        return manager.batchCheckQuantityOfOrders(orders)
    }
}