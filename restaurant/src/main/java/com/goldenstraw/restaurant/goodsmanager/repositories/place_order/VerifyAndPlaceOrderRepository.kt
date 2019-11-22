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
    fun getAllOrderOfDate(condition: String): Observable<MutableList<OrderItem>> {
        return remote.getAllOrderOfDate(condition)
    }

    /**
     * 获取所有供应商
     */
    fun getAllSupplier(): Observable<MutableList<User>> {
        return remote.getAllSupplier()
    }

    /**
     * 删除订单
     */
    fun deleteOrderItem(objectId: String): Completable {
        return remote.deleteOrderItem(objectId)
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
     * 单量验货
     */
    fun setCheckQuantity(newCheckGoods: ObjectCheckGoods, objectId: String): Completable {
        return remote.setCheckQuantity(newCheckGoods, objectId)
    }

    /**
     * 批量确认数量State
     */
    fun checkQuantityOfOrders(orders: BatchOrdersRequest<ObjectState>): Completable {
        return remote.checkQuantityOfOrders(orders)
    }

    /**
     * 提交记帐
     */
    fun commitRecordState(orders: BatchOrdersRequest<ObjectState>): Completable {
        return remote.commitRecordState(orders)
    }


}