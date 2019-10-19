package com.owner.basemodule.room.entities

import androidx.room.*

/**
 * Created by Administrator on 2019/10/12 0012
 */
/**
 * 购物车中的商品
 * Created by Administrator on 2019/9/6 0006
 */
@Entity
data class GoodsOfShoppingCart(
    @PrimaryKey
    var code: String,
    @ColumnInfo
    var goodsName: String,
    @ColumnInfo
    var unitOfMeasurement: String,
    @ColumnInfo
    var unitPrice: Float = 0.0f,
    @ColumnInfo
    var categoryCode: String,
    @ColumnInfo
    var quantity: Int,
    @ColumnInfo
    var note: String = "",
    @ColumnInfo
    var isChecked: Boolean = false
)

@Entity
data class Goods(
    @PrimaryKey
    val objectId: String,
    @ColumnInfo
    var goodsName: String,
    @ColumnInfo
    var unitOfMeasurement: String,
    @ColumnInfo
    var unitPrice: Float = 0.0f,
    @ColumnInfo
    var categoryCode: String,
    @ColumnInfo
    var isChecked: Boolean = false
) {
    @Ignore
    var quantity: Int = 1
}

@Entity
data class GoodsCategory(
    @PrimaryKey
    var objectId: String, //对应远程数据的objectId
    @ColumnInfo
    var categoryName: String,
    @ColumnInfo
    var isSelected: Boolean = false
)