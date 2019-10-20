package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCategory
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import com.owner.basemodule.room.entities.User
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
    fun createGoods(@Body goods: NewGoods): Single<CreateObject>

    //2、增加类别
    @POST("/1/classes/GoodsCategory")
    fun createGoodsCategory(@Body goodsCategory: NewCategory): Single<CreateObject>


    /**
    UPDATE
     */
    //1、更新商品
    @PUT("/1/classes/Goods/{objectId}")
    fun updateGoods(@Body goods: NewGoods, @Path("objectId") code: String): Completable

    //2、更新类别
    @PUT("/1/classes/GoodsCategory/{objectId}")
    fun updateCategory(@Body category: NewCategory, @Path("objectId") code: String): Completable

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

    //3、获取所有商品信息
    @GET("/1/classes/Goods/")
    fun getAllGoods(): Observable<ObjectList<Goods>>

    /**
     * 从用户表中获取供应商信息
     */
    @GET("/1/users")
    fun getAllSupplier(@Query("where") condition: String): Observable<ObjectList<User>>
}