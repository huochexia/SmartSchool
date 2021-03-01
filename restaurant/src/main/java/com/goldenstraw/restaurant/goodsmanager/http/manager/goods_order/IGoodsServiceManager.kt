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
    suspend fun addGoodsToRemote(goods: NewGoods): CreateObject

    //2、增加类别
    suspend fun addCategoryToRemote(category: NewCategory): CreateObject


    /**
     * 更新
     */
    //1、更新商品
    suspend fun updateGoodsToRemote(goods: NewGoods, objectId: String)

    //2、更新类别
    suspend fun updateCategoryToRemote(category: NewCategory, objectId: String)

    /**
     * 查询
     */
    //1、获取所有类别,这里应该剥离出所需要的数据
    suspend fun getAllOfCategory(): ObjectList<GoodsCategory>

    //2、获取某个类别的商品列表
    suspend fun getGoodsOfCategory(category: GoodsCategory): ObjectList<Goods>

    /**
     * 删除
     */
    //1、删除类别
    suspend fun deleteCategory(category: GoodsCategory)

    //2、删除商品
    suspend fun deleteGoods(goods: Goods)

    /**
     * 获取某一天菜单当中菜谱
     */
    suspend fun getCookBookOfDailyMeal(where:String):ObjectList<DailyMeal>


    //获取所有供应商
    fun getAllSupplier(): Observable<MutableList<User>>
}