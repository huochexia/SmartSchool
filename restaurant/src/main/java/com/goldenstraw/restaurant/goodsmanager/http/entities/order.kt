package com.goldenstraw.restaurant.goodsmanager.http.entities

/**
 * 新订单,用于加入新订单
 */
data class NewOrderItem(
    var district: Int,//在网络上存储时需要这个来区分购物车内的商品归属
    var orderDate: String,
    var goodsName: String,
    var unitOfMeasurement: String,
    var unitPrice: Float,
    var categoryCode: String,
    var quantity: Float,
    var note: String = "",
    var state: String = "0"
)


/**
 * 订单、验货单、对帐单三单合一
 */
data class OrderItem(
    val objectId: String,
    var district: Int,//在网络上存储时需要这个来区分购物车内的商品归属
    var orderDate: String,//订单日期
    var goodsName: String,
    var unitOfMeasurement: String,
    var unitPrice: Float,//单价
    var categoryCode: String,
    var quantity: Float, //采购数量
    var checkQuantity: Float = 0.0f,//验货数量
    var againCheckQuantity: Float = 0.0f,//再次确认验货数量
    var note: String,//备注
    var supplier: String? = null, //供货商
    var state: String, //0：新订单，1：验货单，2：结帐单
    var isSelected: Boolean = false
)

/*
  批量修改状态对象
 */
data class ObjectState(
    var state: String
)

/*
批量修改供应商
 */
data class ObjectSupplier(
    var supplier: String
)

/**
 * 批量加入处量时发送的请求体
 */
data class BatchOrderItem<T>(
    val method: String,
    val path: String,
    val body: T
)

data class BatchOrdersRequest<T>(
    val requests: MutableList<BatchOrderItem<T>>
)