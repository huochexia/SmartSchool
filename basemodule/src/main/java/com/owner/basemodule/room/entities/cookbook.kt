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
@Entity(primaryKeys = ["objectId","cb_id", "goods_id"])
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
