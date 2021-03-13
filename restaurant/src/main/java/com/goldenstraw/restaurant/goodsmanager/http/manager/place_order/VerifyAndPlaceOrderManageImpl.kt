package com.goldenstraw.restaurant.goodsmanager.http.manager.place_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.http.service.VerifyAndPlaceOrderApi
import com.owner.basemodule.network.ApiException
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

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
    override fun getAllOrderOfDate(
        condition: String

    ): Observable<MutableList<OrderItem>> {

        return service.getAllOrderOfDate(condition)
            .map {
                if (!it.isSuccess()) {
                    throw ApiException(it.code)
                }
                it.results
            }
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