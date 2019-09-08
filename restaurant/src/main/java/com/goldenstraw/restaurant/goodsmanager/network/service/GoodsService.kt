package com.goldenstraw.restaurant.goodsmanager.network.service

import com.goldenstraw.restaurant.goodsmanager.network.entity.createResponse
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 网络访问数据的接口
 */
interface GoodsApi {

    //增加商品
    @POST("/1/classes/Goods")
    fun createGoods(@Body goods: Goods): Flowable<createResponse>

    //增加类别
    @POST("/1/classes/GoodsCategory")
    fun createGoodsCategory(@Body goodsCategory: GoodsCategory): Flowable<createResponse>

}