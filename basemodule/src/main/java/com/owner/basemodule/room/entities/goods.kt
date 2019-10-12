package com.owner.basemodule.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

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
    var code: String = "",
    @ColumnInfo
    var quantity: Int = 0,//购物车中商品数量，整数购入
    @ColumnInfo
    var userName: String,//在网络上存储时需要这个来区分购物车内的商品归属
    @ColumnInfo
    var goodsName: String,
    @ColumnInfo
    var unitOfMeasurement: String,
    @ColumnInfo
    var unitPrice: Float,
    @ColumnInfo
    var categoryName: String,
    @Ignore
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
)

@Entity
data class GoodsCategory(
    @PrimaryKey
    var objectId: String, //对应远程数据的objectId
    @ColumnInfo
    var categoryName: String,
    @ColumnInfo
    var isSelected: Boolean = false
)