package com.goldenstraw.restaurant.goodsmanager.http.manager

import com.goldenstraw.restaurant.goodsmanager.http.service.ShoppingCartApi

class ShoppingServiceManagerImpl(
    private val serviceApi: ShoppingCartApi
) : IShoppingCartServiceManager {
}