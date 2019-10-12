package com.goldenstraw.restaurant.goodsmanager.repositories

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase

/**
 * Created by Administrator on 2019/10/12 0012
 */

interface ILocalGoodsDataSource :ILocalDataSource

class LocalGoodsDataSourceImpl(
    private val database:AppDatabase
) :ILocalGoodsDataSource