package com.goldenstraw.restaurant.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by Administrator on 2019/9/4 0004
 */
@Entity
data class Goods(
    @PrimaryKey var goodsCode: String,
    @ColumnInfo var goodsName: String,
    @ColumnInfo var unitOfMeasurement: String,
    @ColumnInfo var unitPrice: Float,
    @Ignore var goodsQuantity: Float = 0.0f
)