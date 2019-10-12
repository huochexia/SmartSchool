package com.goldenstraw.restaurant.goodsmanager.http.service

import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.ObjectList
import com.goldenstraw.restaurant.goodsmanager.http.entities.Goods
import com.goldenstraw.restaurant.goodsmanager.http.entities.GoodsCategory
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*

/**
 * 网络访问数据的接口
 */
interface GoodsApi {
    /**
    CREATE
     */
    //1、增加商品,需要加入成功后返回的objectId。因为商品属性中的goodsCode，就是网络数据的objectId
    @POST("/1/classes/Goods")
    fun createGoods(@Body goods: Goods): Single<CreateObject>

    //2、增加类别
    @POST("/1/classes/GoodsCategory")
    fun createGoodsCategory(@Body goodsCategory: GoodsCategory): Single<CreateObject>

    /**
    UPDATE
     */
    //1、更新商品
    @PUT("/1/classes/Goods/{objectId}")
    fun updateGoods(@Body goods: Goods, @Path("objectId") code: String): Completable

    //2、更新类别
    @PUT("/1/classes/GoodsCategory/{objectId}")
    fun updateCategory(@Body category: GoodsCategory, @Path("objectId") code: String): Completable

    /**
    DELETE
     */
    //1、删除商品
    @DELETE("/1/classes/Goods/{objectId}")
    fun deleteGoods(@Path("objectId") objectId: String): Completable

    //2、删除类别
    @DELETE("/1/classes/GoodsCategory/{objectId}")
    fun deleteCategory(@Path("objectId") objectId: String): Completable

    /**
    QUERY OR GET
     */
    //1、获得所有类别
    @GET("/1/classes/GoodsCategory/")
    fun getAllCategory(): Observable<ObjectList<GoodsCategory>>

    //2、得到某个类别的所有商品
    //where = {"categoryCode":"  "}
    @GET("/1/classes/Goods")
    fun getGoodsList(@Query("where") condition: String)
            : Observable<ObjectList<Goods>>

}