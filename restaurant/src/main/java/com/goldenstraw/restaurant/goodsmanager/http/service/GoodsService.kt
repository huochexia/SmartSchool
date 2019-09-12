package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entity.newObject
import com.owner.basemodule.network.HttpResult
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * 网络访问数据的接口
 */
interface GoodsApi {
    /**
    CREATE
     */
    //1、增加商品,需要加入成功后返回的objectId。因为商品属性中的goodsCode，就是网络数据的objectId
    @POST("/1/classes/Goods")
    fun createGoods(@Body goods: Goods): Observable<HttpResult<newObject>>

    //2、增加类别
    @POST("/1/classes/GoodsCategory")
    fun createGoodsCategory(@Body goodsCategory: GoodsCategory): Observable<HttpResult<newObject>>

    /**
    UPDATE
     */
    //1、更新商品
    @PUT("/1/classes/Goods/{objectId}")
    fun updateGoods(@Body goods: Goods, @Path("objectId") code: String):Completable

    //2、更新类别
    @PUT("/1/classes/GoodsCategory/{objectId}")
    fun updateCategory(@Body category: GoodsCategory, @Path("objectId") code: String):Completable


    /**
    DELETE
     */

    /**
    QUERY
     */
}