package com.goldenstraw.restaurant.goodsmanager.http.manager.shoppingcar

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.service.ShoppingCarApi
import com.owner.basemodule.room.entities.NewOrder
import io.reactivex.Completable

class ShoppingServiceManagerImpl(
    private val serviceApi: ShoppingCarApi
) : IShoppingCarServiceManager {

    override fun createNewOrderItemToRemote(orderItem: BatchOrdersRequest<NewOrder>): Completable {
        return serviceApi.batchCreateNewOrderItems(orderItem)
    }
}