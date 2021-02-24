package com.owner.basemodule.room.entities

import androidx.room.*

/**
 * 购物车中的食物，它是由每日菜谱转换而来。清理购物车时就是清理食物。
 * @foodCategory:食物分类：冷菜、热菜、主食、汤粥和明档
 * @foodTime:食物食用时间：早餐、午餐和晚餐
 */
@Entity
data class FoodOfShoppingCar(
    @PrimaryKey
    var foodId: String,
    @ColumnInfo
    var foodName: String,
    @ColumnInfo
    var foodCategory: String,
    @ColumnInfo
    var foodTime: String,
    @ColumnInfo
    var isOfTearcher:Boolean=false //如果是老师的菜，还需要加量

)

/**
 * 购物车中食物的原材料，它是由菜谱中原材料转换而来，增加了采购数量，最终由它转换成订单商品
 * @materialOwnerId:对应的食物即菜谱Id
 * @goodsId:对应的商品Id,
 * @quantity:采购量
 * @ration:定量，原材料以10人份计算，占食品中的比重。
 */
@Entity
data class MaterialOfShoppingCar(
    @PrimaryKey(autoGenerate = true)
    var code: Int = 0,//自身Id
    @ColumnInfo
    var materialOwnerId: String,//对应食物Id
    @ColumnInfo
    var goodsId: String,//商品Id
    @ColumnInfo
    var goodsName: String,
    @ColumnInfo
    var unitOfMeasurement: String,
    @ColumnInfo
    var categoryCode: String,
    @ColumnInfo
    var quantity: Float,//购买数量
    @ColumnInfo
    var ration: Float,//定量，比例量
    @ColumnInfo
    var note: String = ""

)

/**
 * 购物车中食物与原材料关系,通过这个类获取食物和它的原材料列表。
 */
data class FoodWithMaterialsOfShoppingCar(
    @Embedded
    val food: FoodOfShoppingCar,
    @Relation(parentColumn = "foodId", entityColumn = "materialOwnerId")
    val materials: List<MaterialOfShoppingCar>
)

/**
 *从菜谱中原材料转换到购物车中食品原材料的方法，初始数量为0
 */
fun materialToShoppingCar(material: Material): MaterialOfShoppingCar {
    return MaterialOfShoppingCar(
        materialOwnerId = material.materialOwnerId,
        goodsId = material.goodsId,
        goodsName = material.goodsName,
        unitOfMeasurement = material.unitOfMeasurement,
        categoryCode = material.categoryCode,
        quantity = 0.0f,
        ration = material.ration,
    )
}

/**
 * 将商品直接转换成购物车中的原材料，该材料与食物没有关联
 */
fun goodsToShoppingCar(goods: Goods): MaterialOfShoppingCar {
    return MaterialOfShoppingCar(
        materialOwnerId = "common",
        goodsId = goods.objectId,
        goodsName = goods.goodsName,
        unitOfMeasurement = goods.unitOfMeasurement,
        categoryCode = goods.categoryCode,
        quantity = goods.quantity.toFloat(),
        ration = 0.0f
    )
}

