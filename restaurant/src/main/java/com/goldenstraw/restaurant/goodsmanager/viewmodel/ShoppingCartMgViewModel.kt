package com.goldenstraw.restaurant.goodsmanager.viewmodel

import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.repositories.ShoppingCartRepository
import com.owner.basemodule.base.viewmodel.BaseViewModel

class ShoppingCartMgViewModel(
    private val repository: ShoppingCartRepository
) : BaseViewModel() {
}