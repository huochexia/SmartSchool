package com.goldenstraw.restaurant.goodsmanager.http.entity

import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory


/**
 * 生成商品后返回数据
 */
data class NewObject(
    val code: Int = 0,
    val error: String?,
    var createdAt: String,
    var objectId: String
) {
    fun isSuccess(): Boolean = code == 0 && objectId.isNullOrEmpty().not()
}

/**
 * 获取对象列表
 */
class objectList<T>(
    val results: MutableList<T>
)

