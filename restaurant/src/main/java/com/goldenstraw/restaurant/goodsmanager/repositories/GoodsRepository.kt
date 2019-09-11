package com.goldenstraw.restaurant.goodsmanager.repositories

import com.owner.basemodule.base.repository.BaseRepositoryBoth

/**
 * 商品数据源，需要处理来自本地和远程的数据，所以它要继承同时拥有两个数据源的类
 */
class GoodsDataSource(
    remote: IRemoteGoodsDataSource,
    local: ILocalGoodsDataSource
) : BaseRepositoryBoth<IRemoteGoodsDataSource, ILocalGoodsDataSource>(remote, local) {

}