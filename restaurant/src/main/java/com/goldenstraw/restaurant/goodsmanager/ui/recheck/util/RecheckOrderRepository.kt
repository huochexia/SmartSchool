package com.goldenstraw.restaurant.goodsmanager.ui.recheck.util

/**
 * 对应审核和发送订单功能
 */
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.owner.basemodule.base.repository.BaseRepositoryRemote
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

class RecheckOrderRepository(
    private val remote: IRecheckOrderDataSource
) : BaseRepositoryRemote<IRecheckOrderDataSource>(remote) {
    /**
     * 修改订单数量
     */
    fun updateOrderItemQuantity(newQuantity: ObjectReCheck, objectId: String): Completable {
        return remote.updateOrderItemQuantity(newQuantity, objectId)
    }
    /**
     * 批量验货
     */
    fun checkQuantityOfOrders(orders: BatchOrdersRequest<BatchRecheckObject>): Completable {
        return remote.batchRecheckQuantity(orders)
    }
    /**
     *获取某个日期商品订单
     */
    fun getAllOrderOfDate(condition: String): Observable<MutableList<OrderItem>> {
        return remote.getAllOrderOfDate(condition)
    }

    /**
     * 获取所有供应商
     */
    fun getAllSupplier(): Observable<MutableList<User>> {
        return remote.getAllSupplier()
    }

}