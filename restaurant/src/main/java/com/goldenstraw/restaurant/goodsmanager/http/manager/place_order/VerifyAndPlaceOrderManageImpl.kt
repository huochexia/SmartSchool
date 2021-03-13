package com.goldenstraw.restaurant.goodsmanager.http.manager.place_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.http.service.VerifyAndPlaceOrderApi
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable

class VerifyAndPlaceOrderManageImpl(

    private val service: VerifyAndPlaceOrderApi

) : IVerifyAndPlaceOrderManager {

    override fun sendOrdersToSupplier(orders: BatchOrdersRequest<ObjectSupplier>): Completable {
        return service.batchAddOrderToSupplier(orders)
    }

    /**
     * 获取某个日期的全部商品拟购单（state =0)
     * {"$and":[{"wins":{"$gt":150}},{"wins":{"$lt":5}}]}
     */
    override suspend fun getOrdersOfDate(
        condition: String

    ): ObjectList<OrderItem> {
        return service.getOrdersOfDate(condition)
    }

    override suspend fun getSupplier(where:String):ObjectList<User> {

        return service.getSupplier(where)

    }

    override suspend fun deleteOrderItem(objectId: String): DeleteObject {
        return service.deleteOrderItem(objectId)
    }

    override suspend fun updateOrderItemQuantity(
        newQuantity: ObjectQuantity,
        objectId: String
    ): UpdateObject {
        return service.updateOrderItem(newQuantity, objectId)
    }

    override suspend fun setCheckQuantity(newQuantity: ObjectCheckGoods, objectId: String):UpdateObject {
        return service.setCheckQuantity(newQuantity, objectId)
    }

    override fun batchCheckQuantityOfOrders(orders: BatchOrdersRequest<ObjectState>): Completable {
        return service.batchCommitState(orders)
    }

    override fun commitRecordState(orders: BatchOrdersRequest<ObjectState>): Completable {
        return service.batchCommitState(orders)
    }


}