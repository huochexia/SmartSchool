package com.goldenstraw.restaurant.goodsmanager.http.manager.query_orders

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.ObjectList
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
    fun getAllOfOrders(where: String): Observable<MutableList<OrderItem>>

    /**
     * 修改订单
     */
    fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable

    /**
     * 删除未处理订单
     */
    suspend fun deleteOrderItem(objectId: String)

    /**
     * 修改订单数量和备注信息
     */
    suspend fun updateOrderItemQuantityAndNote(newOrder: ObjectQuantityAndNote, objectId: String)

    /**
     * 查询商品信息
     */
    fun getGoodsOfCategory(condition: String): Observable<MutableList<Goods>>

    /**
     * 求和
     */
    fun getTotalOfSupplier(condition: String): Observable<MutableList<SumResult>>

    /**
     * 分组求和
     */
    fun getTotalGroupByName(condition: String): Observable<MutableList<SumByGroup>>

    /**
     * 提交新单价
     */
    fun updateNewPriceOfGoods(newPrice: NewPrice, objectId: String): Completable

    /**
     * 获取一周菜单当中菜谱
     */
     fun getCookBookOfDailyMeal(where:String):Observable<ObjectList<DailyMeal>>
}