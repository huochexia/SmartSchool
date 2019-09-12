package com.goldenstraw.restaurant.goodsmanager.http.manager

import com.goldenstraw.restaurant.goodsmanager.http.entity.newObject
import com.owner.basemodule.network.HttpResult
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * 远程访问数据库，管理商品对象接口
 */
interface IGoodsServiceManager {
    /**
     * 增加
     */
    //1、增加商品
    fun addGoods(goods: Goods): Observable<HttpResult<newObject>>

    //2、增加类别
    fun addCategory(category: GoodsCategory): Observable<HttpResult<newObject>>

    /**
     * 更新
     */
    //1、更新商品
    fun updateGoods(goods: Goods): Completable

    //2、更新类别
    fun updateCategory(category: GoodsCategory): Completable


}