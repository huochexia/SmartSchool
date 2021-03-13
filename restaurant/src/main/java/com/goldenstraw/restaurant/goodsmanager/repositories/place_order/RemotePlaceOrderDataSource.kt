package com.goldenstraw.restaurant.goodsmanager.repositories.place_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.http.manager.place_order.IVerifyAndPlaceOrderManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
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
    fun getAllOrderOfDate(condition: String): Observable<MutableList<OrderItem>>

    /*
    将订单发送给供应商
     */
    fun sendOrdersToSupplier(orders: BatchOrdersRequest<ObjectSupplier>): Completable

    suspend fun getSupplier(where: String): ObjectList<User>

    suspend fun deleteOrderItem(objectId: String): DeleteObject

    suspend fun updateOrderItemQuantity(newQuantity: ObjectQuantity, objectId: String): UpdateObject

    suspend fun setCheckQuantity(newCheckGoods: ObjectCheckGoods, objectId: String): UpdateObject

    fun checkQuantityOfOrders(orders: BatchOrdersRequest<ObjectState>): Completable

    fun commitRecordState(orders: BatchOrdersRequest<ObjectState>): Completable

}

class RemotePlaceOrderDataSourceImpl(
    private val manager: IVerifyAndPlaceOrderManager
) : IRemotePlaceOrderDataSource {

    override fun sendOrdersToSupplier(orders: BatchOrdersRequest<ObjectSupplier>): Completable {
        return manager.sendOrdersToSupplier(orders)
    }

    override fun getAllOrderOfDate(condition: String): Observable<MutableList<OrderItem>> {
        return manager.getAllOrderOfDate(condition)
    }

    override suspend fun getSupplier(where: String): ObjectList<User> {
        return manager.getSupplier(where)
    }

    override suspend fun deleteOrderItem(objectId: String): DeleteObject {
        return manager.deleteOrderItem(objectId)
    }

    override suspend fun updateOrderItemQuantity(
        newQuantity: ObjectQuantity,
        objectId: String
    ): UpdateObject {
        return manager.updateOrderItemQuantity(newQuantity, objectId)
    }

    override suspend fun setCheckQuantity(
        newCheckGoods: ObjectCheckGoods,
        objectId: String
    ): UpdateObject {
        return manager.setCheckQuantity(newCheckGoods, objectId)
    }

    override fun checkQuantityOfOrders(orders: BatchOrdersRequest<ObjectState>): Completable {
        return manager.batchCheckQuantityOfOrders(orders)
    }

    override fun commitRecordState(orders: BatchOrdersRequest<ObjectState>): Completable {
        return manager.commitRecordState(orders)
    }


}