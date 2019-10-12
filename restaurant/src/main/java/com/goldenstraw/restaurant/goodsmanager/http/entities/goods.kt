package com.goldenstraw.restaurant.goodsmanager.http.entities

/**
 * 订单管理中的实体
 * Created by Administrator on 2019/9/6 0006
 */
/**
 * 商品
 */

data class Goods(
    var objectId: String="", //对应远程数据的objectId
    var goodsName: String,
    var unitOfMeasurement: String,
    var unitPrice: Float,
    var categoryCode: String,
    var isChecked: Boolean = false
)

/**
 * 商品分类
 */

data class GoodsCategory(
    var objectId: String="", //对应远程数据的objectId
    var categoryName: String,
    var isSelected: Boolean = false
)


