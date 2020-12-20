package com.owner.basemodule.room.dao

import androidx.room.*
import com.owner.basemodule.room.entities.NewOrder

/**
 * 订单操作
 */
@Dao
interface OrderDao {

    @Query("SELECT * FROM neworder")
    suspend fun getNewOrder(): List<NewOrder>

    @Query("DELETE FROM neworder")
    suspend fun clearNewOrder()

    @Delete
    suspend fun deleteNewOrder(newOrder: NewOrder)

    @Update
    suspend fun updateNewOrder(newOrder: NewOrder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewOrder(list: List<NewOrder>)

}