package com.goldenstraw.restaurant.goodsmanager.http.manager.query_orders

import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.http.service.QueryOrdersApi
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User
import io.reactivex.Observable

class QueryOrdersManagerImpl(
    private val service: QueryOrdersApi
) : IQueryOrdersManager {
    /**
     * 单一条件查询
     * 'where={"score":{"$gte":1000,"$lte":3000}}'
     */
    override suspend fun getAllSupplier(): ObjectList<User> {
        val where = "{\"role\":\"供应商\"}"
        return service.getAllSupplier(where)
    }

    /**
     * 复合查询，供应商和日期
     * 'where={"$and":[{"supplier":XXXXX},{"date":XXXXX}]}'
     *  条件查询：查询小于某日期订单
     *  where1={"orderDate":{"$lt":XXXX}}
     *  表达式：
     *    val where1 = "{\"orderDate\":{\"\$lt\":\"$date\"}}"
     */
    override suspend fun getAllOfOrders(
        where: String
    ): ObjectList<OrderItem> {

        return service.getOrdersList(where)

    }

    /**
     * 修改订单的供应商为空
     */
    override suspend fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): UpdateObject {
        return service.updateOrderOfSupplier(newOrder, objectId)
    }

    /**
     * 获取某类商品信息
     */
    override suspend fun getGoodsOfCategory(condition: String): ObjectList<Goods> {
        return service.getGoodsOfCategory(condition)
    }

    override fun getGoodsFromObjectId(id: String): Observable<Goods> {
        return service.getGoodsFromObjectId(id)
    }

    override suspend fun getTotalOfSupplier(condition: String): ObjectList<SumResult> {
        return service.getTotalOfSupplier(condition = condition)
    }

    override suspend fun getTotalGroupByName(condition: String): ObjectList<SumByGroup> {
        return service.getTotalGroupByName(condition = condition)
    }

    override suspend fun updateNewPriceOfGoods(newPrice: NewPrice, objectId: String) =
        service.updateNewPriceOfGoods(newPrice, objectId)


    override fun getCookBookOfDailyMeal(where: String): Observable<ObjectList<DailyMeal>> {
        return service.getCookBookOfDailyMeal(where)
    }

    override suspend fun deleteOrderItem(objectId: String) =
        service.deleteOrderItem(objectId)


    override suspend fun getOrdersList(where: String): ObjectList<OrderItem> {
        return service.getOrdersList(where)
    }

    override suspend fun updateUnitPrice(newPrice: ObjectUnitPrice, objectId: String) =
        service.updateUnitPriceOfOrders(newPrice, objectId)


    override suspend fun updateOrderItemQuantityAndNote(
        newOrder: ObjectQuantityAndNote,
        objectId: String
    ) :UpdateObject{
        return service.updateOrderItem(newOrder, objectId)
    }
}