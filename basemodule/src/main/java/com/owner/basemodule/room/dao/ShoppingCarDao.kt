package com.owner.basemodule.room.dao

import androidx.room.*
import com.owner.basemodule.room.entities.FoodOfShoppingCar
import com.owner.basemodule.room.entities.FoodWithMaterialsOfShoppingCar
import com.owner.basemodule.room.entities.MaterialOfShoppingCar

/**
 * 处理购物车
 */

@Dao
interface ShoppingCarDao {
    /*
     获取购物车上的食品和它的原材料
     */
    @Transaction
    @Query("SELECT * FROM foodofshoppingcar")
    suspend fun getFoodAndMaterial(): List<FoodWithMaterialsOfShoppingCar>

    @Query("SELECT * FROM MaterialOfShoppingCar WHERE materialOwnerId =:id")
    suspend fun getMaterialOfShopping(id: String): List<MaterialOfShoppingCar>

    /*
    删除购物车里的食品和它的原材料
     */

    @Query("DELETE FROM foodofshoppingcar")
    suspend fun clearFoods()

    @Query("DELETE FROM MaterialOfShoppingCar")
    suspend fun clearMaterials()

    @Delete
    suspend fun deleteMaterial(material: MaterialOfShoppingCar)

    /*
      增加食物和它的材料
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFoodAndMaterial(food: FoodOfShoppingCar, list: List<MaterialOfShoppingCar>)

    /*
    增加与食物没有关联的材料,即通用材料
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMaterialOfShoppingCar(materialList: List<MaterialOfShoppingCar>)

    /*
    保存原材料数量
     */
    @Update
    suspend fun batchUpdateQuantityOfMaterial(list: List<MaterialOfShoppingCar>)

    @Update
    suspend fun updateQuantityOfMaterial(material: MaterialOfShoppingCar)
}