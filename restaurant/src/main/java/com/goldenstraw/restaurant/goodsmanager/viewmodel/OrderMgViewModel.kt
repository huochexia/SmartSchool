package com.goldenstraw.restaurant.goodsmanager.viewmodel

import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.owner.basemodule.base.viewmodel.BaseViewModel

/**
 * 订单管理的ViewModel
 */
class OrderMgViewModel(
    private val repository: GoodsRepository
) : BaseViewModel() {

}