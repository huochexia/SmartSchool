package com.owner.basemodule.room.dao

import androidx.room.*
import com.owner.basemodule.room.entities.CookBookWithMaterials
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

@Dao
interface GoodsDao {
    /**
     * 增加
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoodsListToLocal(goodsList: List<Goods>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryListToLocal(categoryList: List<GoodsCategory>)

    /*
      增加一个，也可以用于修改
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoodsToLocal(goods: Goods)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryToLocal(category: GoodsCategory)

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
    suspend fun getGoodsFromObjectId(id: String): Goods

    /**
     * 使用Flow方式获取数据,因为数据发生变化时要能够被观察到，所以这里将返回值设为Flow。
     */
    @Query("SELECT * FROM goodscategory ORDER BY categoryName")
    fun getAllCategoryFlow(): Flow<List<GoodsCategory>>

    @Query("SELECT * FROM Goods WHERE categoryCode = :categoryId  ORDER BY goodsName")
    fun getGoodsOfCategoryFlow(categoryId: String): Flow<List<Goods>>
    @Query("SELECT * FROM LocalCookBook WHERE cb_id = :objectId")
    fun getCookBookWithMaterials(objectId: String):CookBookWithMaterials
    /*
       使用协程进行模糊查询
     */
    @Query("SELECT * FROM Goods WHERE goodsName LIKE '%' || :name || '%' ORDER BY goodsName")
    suspend fun searchMaterial(name: String): MutableList<Goods>


    /**
     * 删除
     */
    @Delete
    suspend fun deleteGoods(goods: Goods)

    @Delete
    suspend fun deleteCategory(category: GoodsCategory)


    @Query("DELETE FROM goods")
    suspend fun clearGoods()


    @Query("DELETE FROM goodscategory")
    suspend fun clearCategory()
}