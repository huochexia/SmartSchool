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
    val objectId: String,
    @ColumnInfo
    var foodCategory: String,//凉菜，热菜，主食，汤粥，小吃
    @ColumnInfo
    var foodKind: String,//素菜，小荤，大荤
    @ColumnInfo
    var foodName: String

) {
    @Ignore
    var isSelected: Boolean = false
}

/**
 * 菜谱与商品多对多的交叉关系表。
 * 由于Bomb免费限制，一次最多可读500条记录，所以对同步网络关系表产生了难度，无法
 * 实现从网络获取所有关系，然后存入本地。
 * 暂时解决办法是给关系表增加一个字段：类别。按类别分别获取。同时还要限制菜谱对应商品的数量，最多4种。
 */
@Entity(primaryKeys = ["objectId","cb_id", "goods_id"])
data class CookBookGoodsCrossRef(
    val objectId: String,
    val cb_id: String,
    val goods_id: String,
    @ColumnInfo
    val foodCategory: String

)

data class CookBookWithGoods(
    @Embedded
    val cookBook: CookBooks,
    @Relation(
        parentColumn = "cb_id",
        entityColumn = "goods_id",
        associateBy = Junction(CookBookGoodsCrossRef::class)
    )
    val goods: List<Goods>
)
