package com.owner.basemodule.room.dao

import androidx.room.*
import com.owner.basemodule.room.entities.CookBookWithMaterials
import com.owner.basemodule.room.entities.LocalCookBook
import com.owner.basemodule.room.entities.Material

/**
 * 对菜谱的操作。主要是增加和删除功能。增加时要同时增加关系。删除时要同时删除关系。
 *
 */

@Dao
interface CookBookDao {



    /*
    增加菜谱,也可以用于修改菜谱
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCookBook(cookbook: LocalCookBook)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllCookBook(cookbook: MutableList<LocalCookBook>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMaterials(materials: MutableList<Material>)

    /*
    删除菜谱
     */
    @Delete
    suspend fun deleteCookBook(cookbook: LocalCookBook)

    @Delete
    suspend fun deleteMaterialOfCookbook(materials: MutableList<Material>)


    @Query("DELETE FROM localcookbook")
    suspend fun clearCookBook()

    /**
     * 新版本一对多关系，获取某个菜谱和它的原材料
     */
    /*
   获取某类别所有菜谱和它所需的商品
    */
    @Transaction
    @Query("SELECT * FROM LocalCookBook WHERE foodCategory = :category AND isStandby =:used")
    suspend fun getAllCookBookAndMaterials(
        category: String,
        used: Boolean
    ): MutableList<CookBookWithMaterials>

    /*
       获取某个菜谱和它所需的商品
     */
    @Transaction
    @Query("SELECT * FROM LocalCookBook WHERE cb_id =:objectId")
    suspend fun getCookBookAndMaterials(objectId: String): CookBookWithMaterials

    /*
       通过CookBook的名字属性进行模糊查询
     */
    @Transaction
    @Query("SELECT * FROM LocalCookBook WHERE foodname LIKE  '%' || :name || '%' AND foodCategory =:category")
    suspend fun queryCookBookAndMaterials(
        name: String,
        category: String
    ): MutableList<CookBookWithMaterials>

}