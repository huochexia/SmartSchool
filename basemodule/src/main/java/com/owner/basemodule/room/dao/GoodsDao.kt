package com.owner.basemodule.room.dao

import androidx.room.*
import com.owner.basemodule.room.entities.CookBookWithGoods
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

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


    @Query("SELECT * FROM Goods WHERE goodsName LIKE '%' || :name || '%' ORDER BY goodsName")
    fun findByName(name: String): Observable<MutableList<Goods>>

    @Query("SELECT * FROM Goods WHERE goods_id = :id")
    fun getGoodsFromObjectId(id: String): Flowable<Goods>

    /**
     * 使用Flow方式获取数据,因为数据发生变化时要能够被观察到，所以这里将返回值设为Flow。
     */
    @Query("SELECT * FROM goodscategory ORDER BY categoryName")
    fun getAllCategoryFlow(): Flow<List<GoodsCategory>>

    @Query("SELECT * FROM Goods WHERE categoryCode = :categoryId  ORDER BY goodsName")
    fun getGoodsOfCategoryFlow(categoryId: String): Flow<List<Goods>>
    @Query("SELECT * FROM CookBooks WHERE cb_id = :objectId")
    fun getCookBookWithGoods(objectId: String):CookBookWithGoods
    /*
       使用协程进行模糊查询
     */
    @Query("SELECT * FROM Goods WHERE goodsName LIKE '%' || :name || '%' ORDER BY goodsName")
    suspend fun searchMaterial(name: String): MutableList<Goods>

    @Query("SELECT COUNT() FROM GoodsOfShoppingCart")
    fun getShoppingCartOfCount(): Single<Int>

    @Query("SELECT * FROM GoodsOfShoppingCart ORDER BY categoryCode")
    suspend fun getAllShoppingCart(): MutableList<GoodsOfShoppingCart>

    @Query("SELECT  * FROM GoodsOfShoppingCart WHERE foodCategory =:foodCategory ORDER BY categoryCode")
    suspend fun getShoppingCartOfFoodCategory(foodCategory: String): MutableList<GoodsOfShoppingCart>

    /**
     * 删除
     */
    @Delete
    fun deleteGoods(goods: Goods): Completable

    /*
        因为与菜谱的关联关系，所在删除商品时要在关系交叉表中也删除相应的内容
        */
    @Query("DELETE FROM CBGCrossRef WHERE goods_id = :goodsid")
    fun deleteCrossRefOfGoods(goodsid: String): Completable


    @Delete
    fun deleteCategory(category: GoodsCategory): Completable

    @Delete
    fun deleteShoppingCartList(shoppingCart: MutableList<GoodsOfShoppingCart>): Completable

    @Query("DELETE FROM goodsofshoppingcart")
     fun delteAllShoppingCart():Completable


    @Delete
    fun deleteGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable

    @Query("DELETE FROM goods")
    fun clearGoods(): Completable


    @Query("DELETE FROM goodscategory")
    fun clearCategory(): Completable
}