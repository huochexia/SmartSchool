package com.owner.basemodule.room.dao

import androidx.room.*
import com.owner.basemodule.room.entities.CBGCrossRef
import com.owner.basemodule.room.entities.CookBookWithGoods
import com.owner.basemodule.room.entities.CookBooks

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
    @Query("SELECT * FROM CookBooks WHERE foodCategory = :category AND isStandby =:used")
    suspend fun getAllCookBookWithGoods(
        category: String,
        used: Boolean
    ): MutableList<CookBookWithGoods>

    /*
    获取某个菜谱和它所需的商品

     */
    @Transaction
    @Query("SELECT * FROM CookBooks WHERE  cb_id =:objectId")
    suspend fun getCookBookWithGoods(objectId: String): CookBookWithGoods

    /*
     通过CookBook的名字属性进行模糊查询
     */
    @Transaction
    @Query("SELECT * FROM CookBooks WHERE foodname LIKE  '%' || :name || '%' AND foodCategory =:category")
    suspend fun queryCookBookWithGoods(
        name: String,
        category: String
    ): MutableList<CookBookWithGoods>

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
    suspend fun addCrossRef(ref: CBGCrossRef)

    /*
     增加所有关系，这个表的内容暂时是按类别获取的。因为免费Bomb一次只能获取500条记录，
     所以只能按菜谱类别分别获取。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllCrossRef(refList: MutableList<CBGCrossRef>)

    /*
    删除菜谱
     */
    @Delete
    suspend fun deleteCookBook(cookbook: CookBooks)

    @Query("DELETE FROM CookBooks WHERE foodCategory = :category")
    suspend fun deleteCookBookOfCategory(category: String)

    /*
    删除交叉关系
     */
    @Delete
    suspend fun deleteCrossRef(crossRef: CBGCrossRef)

    @Query("DELETE FROM CBGCrossRef WHERE cb_id = :cookbookid ")
    suspend fun deleteCrossRef(cookbookid: String)

    @Query("DELETE FROM CBGCrossRef WHERE foodCategory =:category")
    suspend fun deleteCrossRefOfCategory(category: String)
}