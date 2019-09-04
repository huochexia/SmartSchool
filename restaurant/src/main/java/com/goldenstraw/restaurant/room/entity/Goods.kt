package com.goldenstraw.restaurant.room.entity

/**
 * Created by Administrator on 2019/9/4 0004
 */
data class Goods (var goodsCode:String,
                  var goodsName:String,
                  var unitOfMeasurement:String,
                  var unitPrice:Float,
                  var goodsQuantity:Float = 0.0f)