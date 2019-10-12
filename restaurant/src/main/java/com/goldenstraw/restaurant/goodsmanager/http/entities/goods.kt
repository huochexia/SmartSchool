package com.goldenstraw.restaurant.goodsmanager.http.entities

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
    val categoryCode: String
)



/**
 * 商品分类：NewCategory是发送到网络的对象
 *         GoodsCategory是网络返回的对象
 */
data class NewCategory(
    val categoryName: String
)




