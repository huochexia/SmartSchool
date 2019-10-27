package com.goldenstraw.restaurant.goodsmanager.repositories.queryorders

import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.owner.basemodule.base.repository.BaseRepositoryRemote
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
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

    /**
     *按日期获取供应商订单
     */
    fun getOrdersOfSupplier(where:String): Observable<MutableList<OrderItem>> {
        return remote.getOrdersOfSupplier(where)
    }

    /**
     * 修改订单的供应商
     */
    fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable {
        return remote.updateOrderOfSupplier(newOrder, objectId)
    }

}