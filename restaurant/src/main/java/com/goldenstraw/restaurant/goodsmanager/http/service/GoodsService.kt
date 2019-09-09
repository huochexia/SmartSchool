package com.goldenstraw.restaurant.goodsmanager.http.service

import com.goldenstraw.restaurant.goodsmanager.http.entity.createObject
import com.owner.basemodule.network.HttpResult
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 网络访问数据的接口
 */
interface GoodsApi {

    //增加商品
    @POST("/1/classes/Goods")
    fun createGoods(@Body goods: Goods): Observable<HttpResult<createObject>>

    //增加类别
    @POST("/1/classes/GoodsCategory")
    fun createGoodsCategory(@Body goodsCategory: GoodsCategory): Observable<HttpResult<createObject>>

}