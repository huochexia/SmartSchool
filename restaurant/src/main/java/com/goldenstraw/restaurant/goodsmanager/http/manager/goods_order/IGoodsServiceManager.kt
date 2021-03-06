package com.goldenstraw.restaurant.goodsmanager.http.manager.goods_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCategory
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.DeleteObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.UpdateObject
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.User
import io.reactivex.Observable

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
    suspend fun updateGoodsToRemote(goods: NewGoods, objectId: String): UpdateObject

    //2、更新类别
    suspend fun updateCategoryToRemote(category: NewCategory, objectId: String): UpdateObject

    /**
     * 查询
     */
    //1、获取所有类别,这里应该剥离出所需要的数据
    suspend fun getAllOfCategory(): ObjectList<GoodsCategory>

    //2、获取某个类别的商品列表
    suspend fun getGoodsOfCategory(category: GoodsCategory): ObjectList<Goods>

    //3、分页获取所有商品
    suspend fun getAllOfGoods(skip:Int): ObjectList<Goods>

    /**
     * 删除
     */
    //1、删除类别
    suspend fun deleteCategory(category: GoodsCategory): DeleteObject

    //2、删除商品
    suspend fun deleteGoods(goods: Goods): DeleteObject

    /**
     * 获取某一天菜单当中菜谱
     */
    suspend fun getCookBookOfDailyMeal(where:String):ObjectList<DailyMeal>


    //获取所有供应商
    fun getAllSupplier(): Observable<MutableList<User>>
}