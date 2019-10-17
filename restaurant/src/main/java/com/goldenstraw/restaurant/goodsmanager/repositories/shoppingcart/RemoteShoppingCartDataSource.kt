package com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart

import com.goldenstraw.restaurant.goodsmanager.http.manager.shoppingcart.IShoppingCartServiceManager
import com.owner.basemodule.base.repository.IRemoteDataSource
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable

interface IRemoteShoppingCartDataSource : IRemoteDataSource {
    fun insertGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable
}

class RemoteShoppingCartDataSourceImpl(
    private val service: IShoppingCartServiceManager
) : IRemoteShoppingCartDataSource {
    override fun insertGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable {
        return service.addShoppingCart(goods)
    }

}