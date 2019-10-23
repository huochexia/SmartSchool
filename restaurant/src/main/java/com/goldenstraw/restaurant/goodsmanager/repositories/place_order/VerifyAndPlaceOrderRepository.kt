package com.goldenstraw.restaurant.goodsmanager.repositories.place_order

/**
 * 对应审核和发送订单功能
 */
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectQuantity
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

class VerifyAndPlaceOrderRepository(
    private val remote: IRemotePlaceOrderDataSource,
    private val local: ILocalPlaceOrderDataSource
) : BaseRepositoryBoth<IRemotePlaceOrderDataSource, ILocalPlaceOrderDataSource>(remote, local) {

    /**
     *获取某个日期商品订单
     */
    fun getAllOrderOfDate(date: String): Observable<MutableList<OrderItem>> {
        return remote.getAllOrderOfDate(date)
    }

    /**
     * 获取所有供应商
     */
    fun getAllSupplier(): Observable<MutableList<User>> {
        return remote.getAllSupplier()
    }

    /**
     * 将订单发送给供应商
     */
    fun sendOrdersToSupplier(orders: BatchOrdersRequest<ObjectSupplier>): Completable {
        return remote.sendOrdersToSupplier(orders)
    }

    /**
     * 修改订单数量
     */
    fun updateOrderItemQuantity(newQuantity: ObjectQuantity, objectId: String): Completable {
        return remote.updateOrderItemQuantity(newQuantity, objectId)
    }
}