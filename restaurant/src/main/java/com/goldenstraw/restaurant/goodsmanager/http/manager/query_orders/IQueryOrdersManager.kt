package com.goldenstraw.restaurant.goodsmanager.http.manager.query_orders

import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.http.entities.SumByGroup
import com.goldenstraw.restaurant.goodsmanager.http.entities.SumResult
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

interface IQueryOrdersManager {

    /**
     *  获取所有供应商
     */

    fun getAllSupplier(): Observable<MutableList<User>>

    /**
     * 按日期查询供应商的订单
     */
    fun getOrderOfSupplier(where: String): Observable<MutableList<OrderItem>>

    /**
     * 修改订单
     */
    fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable

    /**
     * 查询商品信息
     */
    fun getGoodsOfCategory(condition: String): Observable<MutableList<Goods>>
    /**
     * 求和
     */
    fun getTotalOfSupplier(condition:String):Observable<MutableList<SumResult>>

    /**
     * 分组求和
     */
    fun getTotalGroupByName(condition: String): Observable<MutableList<SumByGroup>>
}