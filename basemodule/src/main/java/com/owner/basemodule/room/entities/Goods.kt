package com.owner.basemodule.room.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

/**
 * Created by Administrator on 2019/9/4 0004
 * 商品,定义了外键与类别关联起来
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = GoodsCategory::class,
        parentColumns = ["code"],
        childColumns = [("category_code")],
        onDelete = CASCADE
    )]
)
data class Goods(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "goods_code")
    var goodsCode: String,
    @ColumnInfo(name = "goods_name")
    var goodsName: String,
    @ColumnInfo(name = "unit_of_measurement")
    var unitOfMeasurement: String,
    @ColumnInfo(name = "unit_price")
    var unitPrice: Float,
    @ColumnInfo(name = "goods_quantity")
    var goodsQuantity: Float = 0.0f, //商品需要的数量
    @ColumnInfo
    var isChecked: Boolean = false,  //当为真时，说明被选择到购物车当中
    @ColumnInfo(name = "category_code")
    var categoryCode: String
)