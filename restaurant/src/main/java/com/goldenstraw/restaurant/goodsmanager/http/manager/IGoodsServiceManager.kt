package com.goldenstraw.restaurant.goodsmanager.http.manager

import com.goldenstraw.restaurant.goodsmanager.http.entity.createObject
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Observable

/**
 * 远程访问数据库，管理商品对象接口
 */
interface IGoodsServiceManager {
    /**
     * 增加商品
     */
    fun addGoods(goods: Goods): Observable<createObject>

    /**
     * 增加商品类别
     */
    fun addCategory(category: GoodsCategory): Observable<createObject>
}