package com.goldenstraw.restaurant.goodsmanager.repositories.place_order

/**
 * 对应审核和发送订单功能
 */
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable

class VerifyAndPlaceOrderRepository(
    private val remote: IRemotePlaceOrderDataSource,
    private val local: ILocalPlaceOrderDataSource
) : BaseRepositoryBoth<IRemotePlaceOrderDataSource, ILocalPlaceOrderDataSource>(remote, local) {

    /**
     *获取某个日期商品订单
     */
    suspend fun getOrdersOfDate(condition: String): ObjectList<OrderItem> {
        return remote.getOrdersOfDate(condition)
    }

    /**
     * 获取所有供应商
     */
    suspend fun getSupplier(where:String): ObjectList<User> {
        return remote.getSupplier(where)
    }

    /**
     * 删除订单
     */
    suspend fun deleteOrderItem(objectId: String): DeleteObject {
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
    suspend fun updateOrderItemQuantity(newQuantity: ObjectQuantity, objectId: String): UpdateObject {
        return remote.updateOrderItemQuantity(newQuantity, objectId)
    }

    /**
     * 单量验货
     */
    suspend fun setCheckQuantity(newCheckGoods: ObjectCheckGoods, objectId: String): UpdateObject {
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