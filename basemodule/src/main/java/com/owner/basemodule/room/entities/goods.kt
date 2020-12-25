package com.owner.basemodule.room.entities

import androidx.room.*

/**
 * Created by Administrator on 2019/10/12 0012
 */

@Entity
data class Goods(
    @PrimaryKey
    @ColumnInfo(name = "goods_id", typeAffinity = ColumnInfo.TEXT)
    val objectId: String,
    @ColumnInfo
    var goodsName: String,
    @ColumnInfo
    var unitOfMeasurement: String,
    @ColumnInfo
    var unitPrice: Float,
    @ColumnInfo
    var categoryCode: String,
    @ColumnInfo
    var isChecked: Boolean = false,
    @ColumnInfo
    var newPrice: Float = 0.0f //用于存储供应商提交的新价格
) : Comparable<Goods> {
    @Ignore
    var quantity: Int = 0

    override fun compareTo(other: Goods): Int {
        return when {
            categoryCode == other.categoryCode -> {
                0
            }
            categoryCode > other.categoryCode -> {
                1
            }
            else -> {
                -1
            }
        }
    }

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