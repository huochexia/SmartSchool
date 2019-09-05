package com.owner.basemodule.room.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory


/**
 * 辅助类，用于查询某个类的时候同时得到它的所在商品
 */
class CategoryAndAllGoods {
    @Embedded
    lateinit var category: GoodsCategory
    @Relation(parentColumn = "code", entityColumn = "category_code")
    lateinit var goods: List<Goods>
}