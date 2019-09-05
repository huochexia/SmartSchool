package com.owner.basemodule.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 商品分类
 */
@Entity
class GoodsCategory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val code: String,
    @ColumnInfo(name = "category_name")
    var categoryName: String
)