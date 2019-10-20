package com.goldenstraw.restaurant.goodsmanager.http.entities

/**
 * 新订单
 */
data class NewOrderItem(
    var district: Int,//在网络上存储时需要这个来区分购物车内的商品归属
    var orderDate: String,
    var goodsName: String,
    var unitOfMeasurement: String,
    var unitPrice: Float = 0.0f,
    var categoryCode: String,
    var quantity: Int,
    var note: String = ""
)

/**
 * 返回的订单
 */
data class OrderItem(
    val objectId: String,
    var district: Int,//在网络上存储时需要这个来区分购物车内的商品归属
    var orderDate: String,
    var goodsName: String,
    var unitOfMeasurement: String,
    var unitPrice: Float,
    var categoryCode: String,
    var quantity: Int,
    var note: String,
    var isSelected: Boolean = false
)