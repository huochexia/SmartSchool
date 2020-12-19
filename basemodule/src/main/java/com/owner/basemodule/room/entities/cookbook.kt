package com.owner.basemodule.room.entities

import androidx.room.*

/**
 *  菜谱类，一道菜谱由多种商品构成，某种商品对应多个菜谱，两者为多对多关系。
 *  所以需要创建两者之间的交叉关系表。
 */

@Entity
data class CookBooks(
    @PrimaryKey
    @ColumnInfo(name = "cb_id", typeAffinity = ColumnInfo.TEXT)
    var objectId: String,
    @ColumnInfo
    var foodCategory: String,//凉菜，热菜，主食，汤粥，小吃
    @ColumnInfo
    var foodKind: String, //素菜，小荤，大荤
    @ColumnInfo
    var usedNumber:Int ,//使用次数
    @ColumnInfo
    var foodName: String,
    @ColumnInfo
    var isStandby: Boolean //是否备用菜谱

) {
    @Ignore
    var isSelected: Boolean = false
}

/**
 * 菜谱与商品多对多的交叉关系表。
 *
 */
@Entity(primaryKeys = ["objectId","cb_id", "goods_id"],indices = [Index("cb_id"),Index("goods_id")])
data class CBGCrossRef(
    var objectId: String,
    var cb_id: String,
    var goods_id: String,
    @ColumnInfo
    var foodCategory: String

)

data class CookBookWithGoods(
    @Embedded
    val cookBook: CookBooks,
    @Relation(
        parentColumn = "cb_id",
        entityColumn = "goods_id",
        associateBy = Junction(CBGCrossRef::class)
    )
    val goods: List<Goods>
)

/**
 * 原材料，菜谱对应的原材料，它是由商品类转换而来，增加定量即原材料在菜谱中占比（以10人量为依据）
 * @ration:定量
 * @materialOwnerid:对应菜谱Id
 */
@Entity
data class Material(
    @PrimaryKey(autoGenerate = true)
    var objectId:Int =0,//自身Id
    @ColumnInfo
    var materialOwnerId: String,//对应食物Id
    @ColumnInfo
    var goodsId: String,//商品Id
    @ColumnInfo
    var goodsName: String,
    @ColumnInfo
    var unitOfMeasurement: String,
    @ColumnInfo
    var unitPrice: Float,
    @ColumnInfo
    var categoryCode: String,
    @ColumnInfo
    var ration: Float = 0.0f,//定量，比例量

)

/**
 *菜谱与原材料的关系类
 */
data class CookBookWithMaterial(
    @Embedded
    val cookbook: CookBooks,
    @Relation(
        parentColumn = "cb_id",
        entityColumn = "materialOwnerId"
    )
    val materials: List<Material>
)

/**
 *  从商品转换为对应菜谱的原材料
 *  @owner:对应菜谱的Id
 *  @ration:占食物量的比重（以10人量为准）
 */
fun goodsToMaterial(owner:String,goods:Goods,ration:Float=0.0f):Material{
    return Material(
        materialOwnerId = owner,
        goodsId = goods.objectId,
        goodsName = goods.goodsName,
        unitPrice = goods.unitPrice,
        unitOfMeasurement = goods.unitOfMeasurement,
        categoryCode = goods.categoryCode,
        ration = ration
    )
}
