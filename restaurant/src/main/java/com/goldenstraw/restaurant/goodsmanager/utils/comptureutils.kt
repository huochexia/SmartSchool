package com.goldenstraw.restaurant.goodsmanager.utils

import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import java.text.DecimalFormat

/**
 * 合并同类订单
 */
fun mergeSimilarOrderItem(
    it: OrderItem,
    map: HashMap<String, OrderItem>
): String {
    val format = DecimalFormat(".00")
    val key = it.goodsName
    if (map[key] == null) {
        map[key] = it
    } else {
        val oldOrder = map[key]!!
        //数量加
        oldOrder.checkQuantity = oldOrder.checkQuantity.plus(it.checkQuantity)
        //小计四舍五入
        oldOrder.total = format.format(oldOrder.total.plus(it.total)).toFloat()
        //均价=小计/数量，四舍五入
        oldOrder.unitPrice =
            format.format(oldOrder.total / oldOrder.checkQuantity).toFloat()
        map[key] = oldOrder
    }
    return key
}
