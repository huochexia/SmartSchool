package com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewOrderItem
import com.goldenstraw.restaurant.goodsmanager.http.manager.shoppingcart.IShoppingCartServiceManager
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable

interface IRemoteShoppingCartDataSource : IRemoteDataSource {
    fun createNewOrderItem(orderItem: BatchOrdersRequest<NewOrderItem>): Completable
}

class RemoteShoppingCartDataSourceImpl(
    private val service: IShoppingCartServiceManager
) : IRemoteShoppingCartDataSource {
    override fun createNewOrderItem(orderItem: BatchOrdersRequest<NewOrderItem>): Completable {
        return service.createNewOrderItemToRemote(orderItem)
    }

}