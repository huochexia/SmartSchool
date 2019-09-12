package com.owner.basemodule.room.entities

import androidx.room.*

/**
 * 订单管理中的实体
 * Created by Administrator on 2019/9/6 0006
 */
/**
 * 商品
 */
@Entity(
    indices = [Index("category_code")],
    foreignKeys = [ForeignKey(
        entity = GoodsCategory::class,
        parentColumns = ["code"],
        childColumns = [("category_code")],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Goods(
    @PrimaryKey
    var goodsCode: String, //对应远程数据的objectId
    @ColumnInfo(name = "goods_name")
    var goodsName: String,
    @ColumnInfo(name = "unit_of_measurement")
    var unitOfMeasurement: String,
    @ColumnInfo(name = "unit_price")
    var unitPrice: Float,
    @ColumnInfo(name = "category_code")
    var categoryCode: String
) {
    @Ignore
    var isChecked: Boolean = false
}

/**
 * 商品分类
 */
@Entity
data class GoodsCategory(
    @PrimaryKey
    var code: String, //对应远程数据的objectId
    @ColumnInfo(name = "category_name")
    var categoryName: String
)

/**
 * 购物车中的商品
 * Created by Administrator on 2019/9/6 0006
 */
@Entity
data class ShoppingCartGoods(
    @PrimaryKey
    var code: String, //对应远程数据的objectId
    @ColumnInfo
    var quantity: Float = 0.0f,
    @Embedded
    var goods: Goods

)