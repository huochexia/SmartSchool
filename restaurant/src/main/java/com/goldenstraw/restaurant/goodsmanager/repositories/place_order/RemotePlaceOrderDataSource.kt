package com.goldenstraw.restaurant.goodsmanager.repositories.place_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.http.manager.place_order.IVerifyAndPlaceOrderManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import io.reactivex.Observable

/**
 * 远程访问数据
 */
interface IRemotePlaceOrderDataSource : IRemoteDataSource {

    /*
    获取某个日期的商品订单
     */
    fun getAllOrderOfDate(date: String): Observable<MutableList<OrderItem>>
}

class RemotePlaceOrderDataSourceImpl(
    private val manager: IVerifyAndPlaceOrderManager
) : IRemotePlaceOrderDataSource {
    override fun getAllOrderOfDate(date: String): Observable<MutableList<OrderItem>> {
        return manager.getAllOrderOfDate(date)
    }

}