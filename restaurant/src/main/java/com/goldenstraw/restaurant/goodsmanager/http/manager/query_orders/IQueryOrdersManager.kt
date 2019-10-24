package com.goldenstraw.restaurant.goodsmanager.http.manager.query_orders

import com.owner.basemodule.room.entities.User
import io.reactivex.Observable

interface IQueryOrdersManager {

    /**
     *  获取所有供应商
     */

    fun getAllSupplier(): Observable<MutableList<User>>

}