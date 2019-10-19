package com.owner.basemodule.room.dao

import androidx.room.*
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface GoodsDao {
    /**
     * 增加
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoodsList(goodsList: MutableList<Goods>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoodsCategoryList(categoryList: MutableList<GoodsCategory>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShoppingCartGoodsList(shoppingCart: MutableList<GoodsOfShoppingCart>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShoppingCart(shoppingCart: GoodsOfShoppingCart): Completable

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

    @Query("SELECT * FROM Goods WHERE goodsName LIKE '%' || :name || '%' ORDER BY goodsName" )
    fun findByName(name: String): Observable<MutableList<Goods>>

    @Query("SELECT COUNT() FROM GoodsOfShoppingCart")
    fun getShoppingCartOfCount(): Single<Int>

    @Query("SELECT * FROM GoodsOfShoppingCart ORDER BY goodsName")
    fun getAllShoppingCart(): Observable<MutableList<GoodsOfShoppingCart>>

    /**
     * 删除
     */
    @Delete
    fun deleteGoods(goods: Goods): Completable

    @Delete
    fun deleteCategory(category: GoodsCategory): Completable

    @Delete
    fun deleteShoppingCartList(shoppingCart: MutableList<GoodsOfShoppingCart>): Completable

    @Delete
    fun deleteGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable

    @Query("DELETE FROM goods")
    fun clearGoods(): Completable

    @Query("DELETE FROM goodscategory")
    fun clearCategory(): Completable
}