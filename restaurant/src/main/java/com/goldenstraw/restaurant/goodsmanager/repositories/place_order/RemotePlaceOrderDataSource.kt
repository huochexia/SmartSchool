package com.goldenstraw.restaurant.goodsmanager.repositories.place_order

import com.goldenstraw.restaurant.goodsmanager.http.manager.place_order.IVerifyAndPlaceOrderManager
import com.owner.basemodule.base.repository.IRemoteDataSource

/**
 * 远程访问数据
 */
interface IRemotePlaceOrderDataSource : IRemoteDataSource

class RemotePlaceOrderDataSourceImpl (
    private val service:IVerifyAndPlaceOrderManager
): IRemotePlaceOrderDataSource