package com.goldenstraw.restaurant.goodsmanager.http.manager.query_orders

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User
import io.reactivex.Observable

interface IQueryOrdersManager {

    /**
     *  获取所有供应商
     */

    suspend fun getAllSupplier(): ObjectList<User>

    /**
     * 按日期查询供应商的订单
     */
    suspend fun getAllOfOrders(where: String): ObjectList<OrderItem>

    /**
     * 修改订单
     */
    suspend fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): UpdateObject

    /**
     * 删除未处理订单
     */
    suspend fun deleteOrderItem(objectId: String):DeleteObject

    /**
     *  获取某个订单
     */
    suspend fun getOrdersList(where: String): ObjectList<OrderItem>

    /**
     * 修改订单的单价
     */
    suspend fun updateUnitPrice(newPrice: ObjectUnitPrice, objectId: String): UpdateObject

    /**
     * 修改订单数量和备注信息
     */
    suspend fun updateOrderItemQuantityAndNote(newOrder: ObjectQuantityAndNote, objectId: String):UpdateObject

    /**
     * 查询商品信息
     */
    suspend fun getGoodsOfCategory(condition: String): ObjectList<Goods>

    /**
     *获取某个商品信息
     */
    fun getGoodsFromObjectId(id: String): Observable<Goods>

    /**
     * 求和
     */
   suspend fun getTotalOfSupplier(condition: String): ObjectList<SumResult>

    /**
     * 分组求和
     */
    suspend fun getTotalGroupByName(condition: String): ObjectList<SumByGroup>

    /**
     * 提交新单价
     */
    suspend fun updateNewPriceOfGoods(newPrice: NewPrice, objectId: String): UpdateObject

    /**
     * 获取一周菜单当中菜谱
     */
    suspend fun getDailyMeals(where: String): ObjectList<DailyMeal>

    /**
     * 获取菜单当中的菜谱
     */
    suspend fun getCookBook(objectId: String): RemoteCookBook

    /**
     * 获取与材料对应的商品
     */
    suspend fun getGoods(objectId: String):Goods
}