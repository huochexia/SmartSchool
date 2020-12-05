package com.goldenstraw.restaurant.goodsmanager.http.manager.goods_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCategory
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.room.entities.*
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
    fun addGoods(goods: NewGoods): Single<CreateObject>

    //2、增加类别
    fun addCategory(category: NewCategory): Single<CreateObject>


    /**
     * 更新
     */
    //1、更新商品
    fun updateGoods(goods: NewGoods, objectId: String): Completable

    //2、更新类别
    fun updateCategory(category: NewCategory, objectId: String): Completable

    /**
     * 查询
     */
    //1、获取所有类别,这里应该剥离出所需要的数据
    fun getCategory(): Observable<MutableList<GoodsCategory>>

    //2、获取某个类别的商品列表
    fun getGoodsOfCategory(category: GoodsCategory): Observable<MutableList<Goods>>

    //3、获取所有商品信息
    fun getAllGoods(): Observable<MutableList<Goods>>

    //4、获取所有供应商
    fun getAllSupplier(): Observable<MutableList<User>>

    /**
     * 删除
     */
    //1、删除类别
    fun deleteCategory(category: GoodsCategory): Completable

    //2、删除商品
    fun deleteGoods(goods: Goods): Completable

    /**
     * 获取某一天菜单当中菜谱
     */
    suspend fun getCookBookOfDailyMeal(where:String):ObjectList<DailyMeal>

}