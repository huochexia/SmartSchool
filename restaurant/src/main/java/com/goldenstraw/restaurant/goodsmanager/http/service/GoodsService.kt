package com.goldenstraw.restaurant.goodsmanager.http.service

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
    suspend fun createGoods(@Body goods: NewGoods): CreateObject

    //2、增加类别，因为需要用到新增类别在数据库中产生的objectId，所以要有返回值
    @POST("/1/classes/GoodsCategory")
    suspend fun createGoodsCategory(@Body goodsCategory: NewCategory): CreateObject


    /**
    UPDATE
     */
    //1、更新商品
    @PUT("/1/classes/Goods/{objectId}")
    suspend fun updateGoods(@Body goods: NewGoods, @Path("objectId") code: String): UpdateObject

    //2、更新类别
    @PUT("/1/classes/GoodsCategory/{objectId}")
    suspend fun updateCategory(
        @Body category: NewCategory,
        @Path("objectId") code: String
    ): UpdateObject

    /**
    DELETE
     */
    //1、删除商品
    @DELETE("/1/classes/Goods/{objectId}")
    suspend fun deleteGoods(@Path("objectId") objectId: String): DeleteObject

    //2、删除类别
    @DELETE("/1/classes/GoodsCategory/{objectId}")
    suspend fun deleteCategory(@Path("objectId") objectId: String): DeleteObject

    /**
    QUERY OR GET
     */
    //1、获得所有类别
    @GET("/1/classes/GoodsCategory/")
    suspend fun getAllCategory(): ObjectList<GoodsCategory>

    //2、得到某个类别的所有商品
    //where = {"categoryCode":"  "}
    @GET("/1/classes/Goods")
    suspend fun getGoodsList(@Query("where") condition: String, @Query("limit") limit: Int = 500)
            : ObjectList<Goods>

    //3、分页获取全部商品,一次获取200
    @GET("/1/classes/Goods/")
    suspend fun getAllOfGoods(
        @Query("limit") limit: Int = 400,
        @Query("skip") skip: Int
    ): ObjectList<Goods>


    /**
     * 从每日菜单库中查找菜单当中的菜谱
     */
    @GET("/1/classes/DailyMeal")
    suspend fun getCookBookOfDailyMeal(
        @Query("where") condition: String
    ): ObjectList<DailyMeal>


    /**
     * 从用户表中获取供应商信息
     */
    @GET("/1/users")
    fun getAllSupplier(@Query("where") condition: String): Observable<ObjectList<User>>


}