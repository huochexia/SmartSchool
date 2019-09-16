package com.goldenstraw.restaurant.goodsmanager.http.entity

import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory


/**
 * 生成商品后返回数据
 */
data class newObject(
    var createAt: String,
    var updateAt: String,
    var objectId: String
)

/**
 * 获取对象列表
 */
class objectList<T>(
    var results: MutableList<T>
)

