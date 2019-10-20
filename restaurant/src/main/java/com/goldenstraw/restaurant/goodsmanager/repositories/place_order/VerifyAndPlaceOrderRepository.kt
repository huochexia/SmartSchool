package com.goldenstraw.restaurant.goodsmanager.repositories.place_order

/**
 * 对应审核和发送订单功能
 */
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.owner.basemodule.base.repository.BaseRepositoryRemote
import io.reactivex.Observable

class VerifyAndPlaceOrderRepository(
    private val remote: IRemotePlaceOrderDataSource
) : BaseRepositoryRemote<IRemotePlaceOrderDataSource>(remote) {

    /**
     *获取某个日期商品订单
     */
    fun getAllOrderOfDate(date: String): Observable<MutableList<OrderItem>> {
        return remote.getAllOrderOfDate(date)
    }
}