package com.goldenstraw.restaurant.room.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.room.Dao
import androidx.room.Query
import com.goldenstraw.restaurant.room.entity.Goods

/**
 * 本地数据库的商品管理
 * Created by Administrator on 2019/9/5 0005
 */
@Dao
interface GoodsManagerDao {

    @Query("SELECT * FROM goods")
    fun getAllGoods(): LiveData<PagedList<Goods>>

}