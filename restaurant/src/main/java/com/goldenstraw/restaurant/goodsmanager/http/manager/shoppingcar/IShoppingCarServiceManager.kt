package com.goldenstraw.restaurant.goodsmanager.http.manager.shoppingcar

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.owner.basemodule.room.entities.NewOrder
import io.reactivex.Completable

interface IShoppingCarServiceManager {

    //1、增加购物车
    fun createNewOrderItemToRemote(orderItem: BatchOrdersRequest<NewOrder>): Completable
}