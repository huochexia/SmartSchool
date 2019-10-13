package com.owner.basemodule.room.dao

import androidx.room.*
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface GoodsDao {
    /**
     * 增加
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoodsList(goodsList: MutableList<Goods>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoodsCategoryList(categoryList: MutableList<GoodsCategory>): Completable

    /*
      增加一个，也可以用于修改
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewGoods(goods: Goods): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewCategory(category: GoodsCategory): Completable

    /**
     * 获取
     */
    @Query("SELECT * FROM goodscategory ORDER BY categoryName")
    fun getAllCategory(): Observable<MutableList<GoodsCategory>>

    @Query("SELECT * FROM Goods WHERE categoryCode = :code  ORDER BY goodsName")
    fun getAllGoodsOfCategory(code: String): Observable<MutableList<Goods>>

    /**
     * 删除
     */
    @Delete
    fun deleteGoods(goods: Goods): Completable

    @Delete
    fun deleteCategory(category: GoodsCategory): Completable

    @Query("DELETE FROM goods")
    fun clearGoods(): Completable

    @Query("DELETE FROM goodscategory")
    fun clearCategory(): Completable
}