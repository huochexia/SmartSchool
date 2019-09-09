package com.goldenstraw.restaurant.goodsmanager.http.entity

import com.owner.basemodule.room.entities.ShoppingCartGoods
import com.owner.basemodule.room.entities.User

/**
 * 购物车中商品，比本地购物车中的商品多了一个用户属性
 */
data class GoodsOfShoppingCart(
    val user: User,
    val goodsOfShoppingCart: ShoppingCartGoods
)

