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
    var code: String,
    @ColumnInfo
    var goodsName: String,
    @ColumnInfo
    var unitOfMeasurement: String,
    @ColumnInfo
    var unitPrice: Float,
    @ColumnInfo
    var categoryCode: String,
    @ColumnInfo
    var quantity: Float,
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
    var unitPrice: Float,
    @ColumnInfo
    var categoryCode: String,
    @ColumnInfo
    var isChecked: Boolean = false,
    @ColumnInfo
    var newPrice: Float = 0.0f //用于存储供应商提交的新价格
) : Comparable<Goods> {
    @Ignore
    var quantity: Int = 1

    override fun compareTo(other: Goods): Int {
        return if (objectId == other.objectId)
            0
        else
            -1
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