package com.goldenstraw.restaurant.goodsmanager.repositories.place_order

import com.owner.basemodule.base.repository.BaseRepositoryRemote

class VerifyAndPlaceOrderRepository(
    private val remote: IRemotePlaceOrderDataSource
) : BaseRepositoryRemote<IRemotePlaceOrderDataSource>(remote) {
}