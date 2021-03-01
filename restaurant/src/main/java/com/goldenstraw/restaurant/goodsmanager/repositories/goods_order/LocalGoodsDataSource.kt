package com.goldenstraw.restaurant.goodsmanager.repositories.goods_order

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.entities.*
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

/**
 * Created by Administrator on 2019/10/12 0012
 */

interface ILocalGoodsDataSource : ILocalDataSource {
    /**
     * 增加
     */
    suspend fun addGoodsListToLocal(list: List<Goods>)

    suspend fun addCategoryListToLocal(list: List<GoodsCategory>)


    /**
     * 插入
     */
    suspend fun addOrUpdateGoodsToLocal(goods: Goods)

    suspend fun addOrUpdateCategoryToLocal(category: GoodsCategory)

    fun insertSupplierToLocal(supplier: MutableList<User>): Completable

    /**
     * 获取
     */
    fun getAllCategory(): Observable<MutableList<GoodsCategory>>

    fun getGoodsOfCategory(code: String): Observable<MutableList<Goods>>

    suspend fun getGoodsFromObjectId(id: String): Goods

    fun findByName(name: String): Observable<MutableList<Goods>>




    /**
     * 使用Flow方式获取数据
     *
     */

    fun getAllCategoryFlow(): Flow<List<GoodsCategory>>

    fun getGoodsOfCategoryFlow(categoryId: String): Flow<List<Goods>>

    fun getCookBookWithGoods(objectId: String): CookBookWithMaterials

    /**
     * 删除
     */
    suspend fun deleteGoodsFromLocal(goods: Goods)
    suspend fun deleteCategoryFromLocal(category: GoodsCategory)

    suspend fun clearGoodsAll()
    suspend fun clearCategoryAll()
    fun clearUserAll(): Completable

    /**购物车操作**/
    /*
       增加
     */
    //增加食物和它的原材料到购物车当中
    suspend fun addFoodAndMaterial(food: FoodOfShoppingCar, list: List<MaterialOfShoppingCar>)

    suspend fun getNumberOfMaterialOfShoppingCar(): Int


}

class LocalGoodsDataSourceImpl(
    private val database: AppDatabase
) : ILocalGoodsDataSource {

    override suspend fun addCategoryListToLocal(list: List<GoodsCategory>) {
        database.goodsDao().insertCategoryListToLocal(list)
    }

    override suspend fun addOrUpdateCategoryToLocal(category: GoodsCategory) {
        database.goodsDao().insertCategoryToLocal(category)
    }

    override suspend fun addGoodsListToLocal(list: List<Goods>) {
        database.goodsDao().insertGoodsListToLocal(list)
    }

    override suspend fun addOrUpdateGoodsToLocal(goods: Goods) {
        database.goodsDao().insertGoodsToLocal(goods)
    }


    override fun insertSupplierToLocal(supplier: MutableList<User>): Completable {
        return database.userDao().insertUsers(supplier)
    }

    /**
     * 获取
     */
    override fun getAllCategory(): Observable<MutableList<GoodsCategory>> {

        return database.goodsDao().getAllCategory()

    }

    override fun getGoodsOfCategory(code: String): Observable<MutableList<Goods>> {

        return database.goodsDao().getAllGoodsOfCategory(code)

    }

    override suspend fun getGoodsFromObjectId(id: String): Goods {
        return database.goodsDao().getGoodsFromObjectId(id)
    }


    override fun findByName(name: String): Observable<MutableList<Goods>> {
        return database.goodsDao().findByName(name)
    }




    /**
     * 使用Flow方式获取数据
     */
    override fun getAllCategoryFlow(): Flow<List<GoodsCategory>> {
        return database.goodsDao().getAllCategoryFlow()
    }

    override fun getGoodsOfCategoryFlow(categoryId: String): Flow<List<Goods>> {
        return database.goodsDao().getGoodsOfCategoryFlow(categoryId)
    }

    override  fun getCookBookWithGoods(objectId: String): CookBookWithMaterials {
        return database.goodsDao().getCookBookWithMaterials(objectId)
    }

    /**
     * 删除
     */
    override suspend fun deleteCategoryFromLocal(category: GoodsCategory) {
        database.goodsDao().deleteCategory(category)
    }

    override suspend fun deleteGoodsFromLocal(goods: Goods) {
        database.goodsDao().deleteGoods(goods)
    }


    override suspend fun clearCategoryAll() {
        database.goodsDao().clearCategory()
    }

    override suspend fun clearGoodsAll() {
        database.goodsDao().clearGoods()
    }

    override fun clearUserAll(): Completable {
        return database.userDao().clearUser()
    }

    /**购物车部分**/

    override suspend fun addFoodAndMaterial(
        food: FoodOfShoppingCar,
        list: List<MaterialOfShoppingCar>
    ) {
        database.shoppingCarDao().addFoodAndMaterial(food, list)
    }
    /*
      购物车原材料数量
     */
    override suspend fun getNumberOfMaterialOfShoppingCar(): Int {
        return database.shoppingCarDao().getNumberOfMaterialOfShoppingCar()
    }

}