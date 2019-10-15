package com.goldenstraw.restaurant.goodsmanager.repositories

import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Observable

class ShoppingCartRepository(
    val remote: IRemoteShoppingCartDataSource,
    val local: ILocalShoppingCartDataSource
) : BaseRepositoryBoth<IRemoteShoppingCartDataSource, ILocalShoppingCartDataSource>(remote, local) {
    /*
      获取购物车内所有商品
     */
    fun getAllShoppingCart(): Observable<MutableList<GoodsOfShoppingCart>> {
        return local.getAllGoods()
    }
}