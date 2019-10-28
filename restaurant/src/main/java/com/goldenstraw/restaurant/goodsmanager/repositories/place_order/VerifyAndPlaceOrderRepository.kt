package com.goldenstraw.restaurant.goodsmanager.repositories.place_order

/**
 * 对应审核和发送订单功能
 */
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
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
    fun getAllOrderOfDate(date: String,state:Int): Observable<MutableList<OrderItem>> {
        return remote.getAllOrderOfDate(date,state)
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

    /**
     * 验货
     */
    fun checkQuantityOfOrders(orders: BatchOrdersRequest<ObjectCheckGoods>): Completable {
        return remote.checkQuantityOfOrders(orders)
    }
}