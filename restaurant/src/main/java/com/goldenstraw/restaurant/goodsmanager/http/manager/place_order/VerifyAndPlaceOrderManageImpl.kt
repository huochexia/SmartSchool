package com.goldenstraw.restaurant.goodsmanager.http.manager.place_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.http.service.VerifyAndPlaceOrderApi
import com.owner.basemodule.network.ApiException
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

    override fun getAllSupplier(): Observable<MutableList<User>> {
        val where = """{"role":"供应商"}"""
        return service.getAllSupplier(where).map {
            if (!it.isSuccess()) {
                throw ApiException(it.code)
            }
            it.results
        }
    }

    override fun deleteOrderItem(objectId: String): Completable {
        return service.deleteOrderItem(objectId)
    }

    override fun updateOrderItemQuantity(
        newQuantity: ObjectQuantity,
        objectId: String
    ): Completable {
        return service.updateOrderItem(newQuantity, objectId)
    }

    override fun setCheckQuantity(newQuantity: ObjectCheckGoods, objectId: String): Completable {
        return service.setCheckQuantity(newQuantity, objectId)
    }

    override fun batchCheckQuantityOfOrders(orders: BatchOrdersRequest<ObjectState>): Completable {
        return service.batchCommitState(orders)
    }

    override fun commitRecordState(orders: BatchOrdersRequest<ObjectState>): Completable {
        return service.batchCommitState(orders)
    }


}