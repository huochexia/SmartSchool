package com.goldenstraw.restaurant.goodsmanager.http.service

import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.room.entities.User
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 网络访问Api
 */
interface QueryOrdersApi{
    /**
     * 从用户表中获取供应商信息
     */
    @GET("/1/users")
    fun getAllSupplier(@Query("where") condition: String): Observable<ObjectList<User>>

}