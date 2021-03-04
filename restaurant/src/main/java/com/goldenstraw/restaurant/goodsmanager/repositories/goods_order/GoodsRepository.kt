package com.goldenstraw.restaurant.goodsmanager.repositories.goods_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCategory
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.network.ApiException
import com.owner.basemodule.network.CreateObject
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.room.entities.*
import kotlinx.coroutines.flow.Flow

/**
 * 商品数据源，需要处理来自本地和远程的数据，所以它要继承同时拥有两个数据源的类
 */
class GoodsRepository(
    private val remote: IRemoteGoodsDataSource,
    private val local: ILocalGoodsDataSource
) : BaseRepositoryBoth<IRemoteGoodsDataSource, ILocalGoodsDataSource>(remote, local) {


    /********************************************************
     * 远程数据操作
     ********************************************************/
    /**
     * 增加
     */
    //1、 增加商品到远程数据库,成功后取出objectId，赋值给Goods对象，然后保存本地库

    suspend fun addGoodsToRemote(goods: NewGoods): CreateObject {
        return remote.addGoodsToRemote(goods)
    }

    //2、增加商品类别到远程数据库。

    suspend fun addCategoryToRemote(category: NewCategory): CreateObject {
        return remote.addCategoryToRemote(category)
    }

    /**
     * 更新
     */
    //1、更新远程数据
    suspend fun updateGoodsToRemote(goods: Goods) {
        val updateGoods = NewGoods(
            goodsName = goods.goodsName,
            unitOfMeasurement = goods.unitOfMeasurement,
            categoryCode = goods.categoryCode,
            unitPrice = goods.unitPrice
        )
        remote.updateGoodsToRemote(updateGoods, goods.objectId)

    }

    //2、更新类别
    suspend fun updateCategoryToRemote(category: GoodsCategory) {
        val updateCategory = NewCategory(
            categoryName = category.categoryName
        )
        remote.updateCategoryToRemote(updateCategory, category.objectId)
    }

    /*
     * 获取远程数据
     */
    suspend fun getAllCategoryFromNetwork(): ObjectList<GoodsCategory> {
        return remote.getAllOfCategory()
    }

    suspend fun getGoodsOfCategoryFromNetwork(category: GoodsCategory): ObjectList<Goods> {
        return remote.getGoodsOfCategory(category)
    }

    suspend fun getAllOfGoods(skip: Int): ObjectList<Goods> {
        return remote.getAllOfGoods(skip)
    }

    /*
     * 删除
     */
    suspend fun deleteGoodsFromRemote(goods: Goods) {
        remote.deleteGoods(goods)
    }

    suspend fun deleteCategoryFromRemote(category: GoodsCategory) {
        remote.deleteCategory(category)
    }

    /*
     * 获取某日菜单
     */
    suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal> {
        return remote.getDailyMealOfDate(where)
    }

    /*****************************************
     * 本地数据
     *****************************************/
    /*
         将商品加入到本地
        */
    suspend fun addGoodsListToLocal(list: List<Goods>) {
        local.addGoodsListToLocal(list)
    }

    suspend fun addOrUpdateGoodsToLocal(goods: Goods) {
        local.addOrUpdateGoodsToLocal(goods)
    }


    /*
      将商品类别加入本地
     */
    suspend fun addCategoryListToLocal(list: MutableList<GoodsCategory>) {
        local.addCategoryListToLocal(list)
    }

    suspend fun addOrUpdateCategoryToLocal(category: GoodsCategory) {
        local.addOrUpdateCategoryToLocal(category)
    }


    val categoryFlowFromLocal = local.getAllCategoryFlow()


    fun getGoodsOfCategoryFromLocalFlow(categoryId: String): Flow<List<Goods>> {
        return local.getGoodsOfCategoryFlow(categoryId)
    }

    fun getCookBookWithMaterials(objectId: String): CookBookWithMaterials {
        return local.getCookBookWithGoods(objectId)
    }

    /*
      根据名字模糊查找商品
     */
    fun findByName(name: String): Flow<MutableList<Goods>> {
        return local.findByName(name)
    }

    /*
     * 删除
     */
    suspend fun deleteGoodsFromLocal(goods: Goods) {
        local.deleteGoodsFromLocal(goods)
    }

    suspend fun deleteCategoryFromLocal(categroy: GoodsCategory) {
        return local.deleteCategoryFromLocal(categroy)
    }


    /*******************************************************
     * 同步数据
     *******************************************************/
    suspend fun clearLocalData() {
        local.clearGoodsAll()
        local.clearCategoryAll()
    }

    suspend fun syncCategory() {
        val result = getAllCategoryFromNetwork()
        if (!result.isSuccess()) {
            throw ApiException(result.code)
        } else {
            addCategoryListToLocal(result.results!!)
        }
    }

    suspend fun syncGoods() {
        var skip = 0
        val goodsList = mutableListOf<Goods>()
        var isContinue = true
        while (isContinue) {
            val result = getAllOfGoods(skip)
            if (!result.isSuccess()) {
                throw ApiException(result.code)
            } else {
                goodsList.addAll(result.results!!)
                if (result.results!!.size == 400) {
                    skip += 400
                } else {
                    isContinue = false
                }
            }
        }
        addGoodsListToLocal(goodsList)
    }

    /***********************************************
     * 新版本对购物车的操作
     ***********************************************/
    suspend fun addFoodAndMaterialsToShoppingCar(
        food: FoodOfShoppingCar,
        materialList: List<MaterialOfShoppingCar>
    ) {
        local.addFoodAndMaterial(food, materialList)
    }


}