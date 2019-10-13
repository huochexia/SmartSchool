package com.goldenstraw.restaurant.goodsmanager.repositories

import com.goldenstraw.restaurant.goodsmanager.http.manager.IShoppingCartServiceManager
import com.owner.basemodule.base.repository.IRemoteDataSource

interface IRemoteShoppingCartDataSource : IRemoteDataSource

class RemoteShoppingCartDataSourceImpl(
    private val service: IShoppingCartServiceManager
) : IRemoteShoppingCartDataSource