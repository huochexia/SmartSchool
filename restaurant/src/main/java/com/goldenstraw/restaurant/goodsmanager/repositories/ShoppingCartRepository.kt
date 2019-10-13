package com.goldenstraw.restaurant.goodsmanager.repositories

import com.owner.basemodule.base.repository.BaseRepositoryBoth

class ShoppingCartRepository(
    private val remote: IRemoteShoppingCartDataSource,
    private val local: ILocalShoppingCartDataSource
) : BaseRepositoryBoth<IRemoteShoppingCartDataSource, ILocalShoppingCartDataSource>(remote, local) {
}