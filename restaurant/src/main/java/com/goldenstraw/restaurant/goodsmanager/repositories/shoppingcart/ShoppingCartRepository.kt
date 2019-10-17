package com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart

import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart.ILocalShoppingCartDataSource
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart.IRemoteShoppingCartDataSource
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable
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

    fun addGoodsOfShoppingCartToRemote(goods: GoodsOfShoppingCart): Completable {

        return remote.insertGoodsOfShoppingCart(goods)
    }

    fun deleteGoodsOfShoppingCartFromLocal(goods: GoodsOfShoppingCart): Completable {
        return local.deleteGoodsOfShoppingCart(goods)
    }
}