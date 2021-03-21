package com.owner.basemodule.room.entities

import androidx.room.*
import java.io.Serializable

/**
 *  菜谱类，一道菜谱由多种商品构成，某种商品对应多个菜谱，两者为多对多关系。
 *  所以需要创建两者之间的交叉关系表。
 */

@Entity
data class LocalCookBook(
    @PrimaryKey
    @ColumnInfo(name = "cb_id", typeAffinity = ColumnInfo.TEXT)
    var objectId: String,
    @ColumnInfo
    var foodCategory: String,//凉菜，热菜，主食，汤粥，小吃
    @ColumnInfo
    var foodKind: String, //素菜，小荤，大荤
    @ColumnInfo
    var usedNumber: Int = 0,//使用次数
    @ColumnInfo
    var foodName: String,
    @ColumnInfo
    var isStandby: Boolean = false, //是否备用菜谱

) : Serializable {
    @Ignore
    var isSelected: Boolean = false
}


/**
 * 原材料，菜谱对应的原材料，它是由商品类转换而来，增加定量即原材料在菜谱中占比（以10人量为依据）
 * @ration:定量
 * @materialOwnerid:对应菜谱Id
 */
@Entity
data class Material(
    @PrimaryKey
    var objectId: String = System.currentTimeMillis().toString(),//用系统时间保证它的唯一性
    @ColumnInfo
    var materialOwnerId: String = "",//对应食物Id
    @ColumnInfo
    var goodsId: String,//商品Id
    @ColumnInfo
    var goodsName: String,
    @ColumnInfo
    var unitOfMeasurement: String,
    @ColumnInfo
    var categoryCode: String,
    @ColumnInfo
    var ration: Float = 0.0f,//定量，比例量

)

/**
 *菜谱与原材料的关系类
 */
data class CookBookWithMaterials(
    @Embedded
    val cookbook: LocalCookBook,
    @Relation(
        parentColumn = "cb_id",
        entityColumn = "materialOwnerId"
    )
    val materials: List<Material>
)

/**
 *  从商品转换为对应菜谱的原材料
 *  @foodId:对应菜谱的Id
 *  @ration:占食物量的比重（以10人量为准）
 */
fun goodsToMaterial(goods: Goods): Material {
    return Material(
        goodsId = goods.objectId,
        goodsName = goods.goodsName,
        unitOfMeasurement = goods.unitOfMeasurement,
        categoryCode = goods.categoryCode,
    )

}
