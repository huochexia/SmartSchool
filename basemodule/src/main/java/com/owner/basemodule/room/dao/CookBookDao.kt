package com.owner.basemodule.room.dao

import androidx.room.*
import com.owner.basemodule.room.entities.CookBookGoodsCrossRef
import com.owner.basemodule.room.entities.CookBookWithGoods
import com.owner.basemodule.room.entities.CookBooks
import kotlinx.coroutines.flow.Flow

/**
 * 对菜谱的操作。主要是增加和删除功能。增加时要同时增加关系。删除时要同时删除关系。
 *
 */

@Dao
interface CookBookDao {
    /*
    获取某类别所有菜谱和它所需的商品
     */
    @Transaction
    @Query("SELECT * FROM CookBooks WHERE foodCategory = :category")
    fun getAllCookBookWithGoods(category: String): Flow<MutableList<CookBookWithGoods>>

    /*
     通过CookBook的名字属性进行模糊查询
     */
    @Transaction
    @Query("SELECT * FROM CookBooks WHERE foodname LIKE  '%' || :name || '%' ORDER BY foodCategory")
    fun queryCookBookWithGoods(name: String): Flow<MutableList<CookBookWithGoods>>

    /*
    增加菜谱,也可以用于修改菜谱
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCookBook(cookbook: CookBooks)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllCookBook(cookbook: MutableList<CookBooks>)

    /*
    增加交叉关系
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCrossRef(ref: CookBookGoodsCrossRef)

    /*
     增加所有关系，这个表的内容暂时是按类别获取的。因为免费Bomb一次只能获取500条记录，
     所以只能按菜谱类别分别获取。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllCrossRef(refList: MutableList<CookBookGoodsCrossRef>)

    /*
    删除菜谱
     */
    @Delete
    suspend fun deleteCookBook(cookbook: CookBooks)
    /*
    删除交叉关系
     */
    @Delete
    suspend fun deleteCrossRef(crossRef: CookBookGoodsCrossRef)

    @Query("DELETE FROM CookBookGoodsCrossRef WHERE cb_id = :cookbookid AND goods_id =:goodsid")
    suspend fun deleteCrossRef(cookbookid: String, goodsid: String)


}