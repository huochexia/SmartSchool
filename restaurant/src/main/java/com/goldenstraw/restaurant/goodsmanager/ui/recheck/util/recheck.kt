package com.goldenstraw.restaurant.goodsmanager.ui.recheck.util




data class ObjectReCheck(
    var againCheckQuantity: Float,
    var reQuantity:Float,
    var againTotal:Float
)
/*

 */
data class BatchRecheckObject(
    var quantity:Float,
    var checkQuantity:Float,
    var total:Float,
    var againTotal:Float,
    var state:Int
)

data class SupplierOfTotal(
    var _sumTotal: Float,
    var _sumAgainTotal: Float,
    var supplier: String
)

