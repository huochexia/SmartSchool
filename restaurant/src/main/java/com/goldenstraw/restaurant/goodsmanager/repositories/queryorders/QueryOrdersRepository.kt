package com.goldenstraw.restaurant.goodsmanager.repositories.queryorders

import com.owner.basemodule.base.repository.BaseRepositoryRemote
import com.owner.basemodule.room.entities.User
import io.reactivex.Observable

class QueryOrdersRepository(
    private val remote: IRemoteQueryOrdersDataSource
) : BaseRepositoryRemote<IRemoteQueryOrdersDataSource>(remote) {

    /**
     * 获取所有供应商
     */
    fun getAllSupplier(): Observable<MutableList<User>> {
        return remote.getAllSupplier()
    }
}