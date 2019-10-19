package com.goldenstraw.restaurant.goodsmanager.http.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * 订单管理中的实体
 * Created by Administrator on 2019/9/6 0006
 */
/**
 * 商品:NewGoods是发送到网络的对象，没有objectId。因为objectId是网络数据保留属性，所以不能定义
 *     Goods是从网络获得的对象，有网络自动生成的objectId
 */
data class NewGoods(
    val goodsName: String,
    val unitOfMeasurement: String,
    val categoryCode: String,
    val unitPrice: Float = 0.0f
)


/**
 * 商品分类：NewCategory是发送到网络的对象
 *         GoodsCategory是网络返回的对象
 */
data class NewCategory(
    val categoryName: String
)

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




