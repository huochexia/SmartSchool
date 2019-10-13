package com.goldenstraw.restaurant.goodsmanager.repositories

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase

interface ILocalShoppingCartDataSource : ILocalDataSource

class LocalShoppingCartDataSourceImpl(
    private val database: AppDatabase
) : ILocalShoppingCartDataSource