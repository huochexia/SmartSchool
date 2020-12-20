package com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcar

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewOrderItem
import com.goldenstraw.restaurant.goodsmanager.http.manager.shoppingcar.IShoppingCarServiceManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.room.entities.NewOrder
import io.reactivex.Completable

interface IRemoteShoppingCarDataSource : IRemoteDataSource {
    fun createNewOrderItem(orderItem: BatchOrdersRequest<NewOrder>): Completable
}

class RemoteShoppingCarDataSourceImpl(
    private val service: IShoppingCarServiceManager
) : IRemoteShoppingCarDataSource {
    override fun createNewOrderItem(orderItem: BatchOrdersRequest<NewOrder>): Completable {
        return service.createNewOrderItemToRemote(orderItem)
    }

}