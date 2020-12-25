package com.goldenstraw.restaurant.goodsmanager.http.entities


/**
 * 订单、验货单、对帐单三单合一
 */
data class OrderItem(
    val objectId: String,
    var district: Int,//在网络上存储时需要这个来区分购物车内的商品归属
    var orderDate: String,//订单日期
    var goodsName: String,
    var unitOfMeasurement: String,
    var categoryCode: String,
    var unitPrice: Float,//单价
    var quantity: Float, //采购数量
    var checkQuantity: Float = 0.0f,//验货数量
    var total: Float,
    var againCheckQuantity: Float = 0.0f,//再次确认验货数量
    var note: String,//备注
    var supplier: String? = null, //供货商
    var state: Int, //0：拟购单，1：订货单，2：验货单，3：确认单，4：记帐单
    var isSelected: Boolean = false
)

/*
  批量修改状态对象
 */
data class ObjectState(
    var state: Int
)

/*
   修改订单数量
 */
data class ObjectQuantity(
    var quantity: Float
)
/*
  修改订单单价
 */
data class ObjectUnitPrice(
    var unitPrice: Float
)

/*
    修改订单数量和备注
 */
data class ObjectQuantityAndNote(
    var quantity: Float,
    var note: String
)

/*
验货,确定数量，验货日期，改变状态为2
 */
data class ObjectCheckGoods(
    var quantity: Float,
    var checkQuantity: Float,
    var againCheckQuantity: Float,
    var total: Float,//四舍五入，保留两位数，
    var state: Int
)

/*
批量修改供应商
 */
data class ObjectSupplier(
    var supplier: String,
    var state: Int
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

/**
 *求和结果
 */
data class SumResult(
    val _sumTotal: Float
)
/**
 * 分组求和
 */
data class SumByGroup(
    val _sumCheckQuantity :Float,
    val _sumTotal: Float,
    val goodsName:String
)
