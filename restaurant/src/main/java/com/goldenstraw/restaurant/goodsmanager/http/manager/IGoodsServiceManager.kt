package com.goldenstraw.restaurant.goodsmanager.http.manager

import com.goldenstraw.restaurant.goodsmanager.http.entity.NewObject
import com.goldenstraw.restaurant.goodsmanager.http.entity.objectList
import com.owner.basemodule.network.HttpResult
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * 远程访问数据库，管理商品对象接口
 */
interface IGoodsServiceManager {
    /**
     * 增加
     */
    //1、增加商品
    fun addGoods(goods: Goods): Single<NewObject>

    //2、增加类别
    fun addCategory(category: GoodsCategory): Single<NewObject>

    /**
     * 更新
     */
    //1、更新商品
    fun updateGoods(goods: Goods): Completable

    //2、更新类别
    fun updateCategory(category: GoodsCategory): Completable

    /**
     * 查询
     */
    //1、获取所有类别
    fun getCategory(): Observable<HttpResult<objectList<GoodsCategory>>>

    //2、获取某个类别的商品列表
    fun getGoodsOfCategory(category: GoodsCategory): Observable<HttpResult<objectList<Goods>>>

    /**
     * 删除
     */
    //1、删除类别
    fun deleteCategory(category:GoodsCategory): Completable

    //2、删除商品
    fun deleteGoods(goods:Goods): Completable

}