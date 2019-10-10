package com.owner.basemodule.room.entities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    var goodsCode: String = "", //对应远程数据的objectId
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
    var code: String = "", //对应远程数据的objectId
    @ColumnInfo(name = "category_name")
    var categoryName: String
) {
    @Ignore
    var isSelected = false
}

/**
 * 购物车中的商品
 * Created by Administrator on 2019/9/6 0006
 */
@Entity
data class GoodsOfShoppingCart(
    @PrimaryKey
    var code: String = "", //对应远程数据的objectId
    @ColumnInfo
    var quantity: Int = 0,//购物车中商品数量，整数购入
    @ColumnInfo
    var userName: String,//在网络上存储时需要这个来区分购物车内的商品归属
    @Embedded
    var goods: Goods

)