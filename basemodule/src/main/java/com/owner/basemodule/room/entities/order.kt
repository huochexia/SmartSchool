package com.owner.basemodule.room.entities

import androidx.room.*
import com.owner.basemodule.util.TimeConverter

/**
 * 购物车当中的原材料，汇总去重后形成订单对象，它与商品一对一，获取商品的单价。后上传网络
 */
@Entity
data class NewOrder(
    @PrimaryKey
    @ColumnInfo
    var goodsId: String,
    @ColumnInfo
    var goodsName: String,
    @ColumnInfo
    var district: Int = 0,//在网络上存储时需要这个来区分购物车内的商品归属
    @ColumnInfo
    var orderDate: String,
    @ColumnInfo
    var unitOfMeasurement: String,
    @ColumnInfo
    var unitPrice: Float = 0.0f,
    @ColumnInfo
    var categoryCode: String,
    @ColumnInfo
    var quantity: Float,
    @ColumnInfo
    var checkQuantity: Float = 0.0f,//验货数量
    @ColumnInfo
    var againCheckQuantity: Float = 0.0f,//再次确认验货数量
    @ColumnInfo
    var total: Float = 0.0f,
    @ColumnInfo
    var note: String = "",
    @ColumnInfo
    var state: Int = 0 //订单状态：0订，1送，2验，3定，4记
)


/**
 * 购物车原材料转换初始订单
 */
fun materialToOrder(material: MaterialOfShoppingCar, district: Int): NewOrder {
    return NewOrder(
        goodsId = material.goodsId,
        goodsName = material.goodsName,
        district = district,
        orderDate = TimeConverter.getCurrentDateString(),
        unitOfMeasurement = material.unitOfMeasurement,
        categoryCode = material.categoryCode,
        quantity = material.quantity,
        note = material.note
    )
}