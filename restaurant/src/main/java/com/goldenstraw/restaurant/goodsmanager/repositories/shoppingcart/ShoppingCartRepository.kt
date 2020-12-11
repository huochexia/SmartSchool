package com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewOrderItem
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable

class ShoppingCartRepository(
    private val remote: IRemoteShoppingCartDataSource,
    private val local: ILocalShoppingCartDataSource
) : BaseRepositoryBoth<IRemoteShoppingCartDataSource, ILocalShoppingCartDataSource>(remote, local) {
    /*
      获取购物车内所有商品
     */
    suspend fun getAllOfShoppingCart(): MutableList<GoodsOfShoppingCart> {

        return local.getAllGoodsOfFoodCategory()
    }

    fun deleteGoodsOfShoppingCartFromLocal(goods: GoodsOfShoppingCart): Completable {
        return local.deleteGoodsOfShoppingCart(goods)
    }

    fun deleteGoodsOfShoppingCartListFromLocal(list: MutableList<GoodsOfShoppingCart>): Completable {
        return local.deleteShoppingCartList(list)
    }

    fun deleteAllOfShoppingCart(): Completable {
        return local.deleteAllShoppingCart()
    }

    fun updateGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable {
        return local.updateGoodsOfShoppingCart(goods)
    }

    suspend fun getGoodsOfShoppingCart(foodCategory: String): MutableList<GoodsOfShoppingCart> {
        return local.getGoodsOfFoodCategory(foodCategory)
    }
    /*
     将本地购物车中商品提交成订单
     */

    fun createNewOrderItem(orderItem: BatchOrdersRequest<NewOrderItem>): Completable {

        return remote.createNewOrderItem(orderItem)
    }
}