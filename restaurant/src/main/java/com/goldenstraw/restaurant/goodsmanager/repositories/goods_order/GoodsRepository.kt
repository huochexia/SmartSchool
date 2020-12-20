package com.goldenstraw.restaurant.goodsmanager.repositories.goods_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCategory
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.network.ApiException
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.room.entities.*
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

/**
 * 商品数据源，需要处理来自本地和远程的数据，所以它要继承同时拥有两个数据源的类
 */
class GoodsRepository(
    private val remote: IRemoteGoodsDataSource,
    private val local: ILocalGoodsDataSource
) : BaseRepositoryBoth<IRemoteGoodsDataSource, ILocalGoodsDataSource>(remote, local) {

    /**
     * 增加
     */
    //1、 增加商品到远程数据库,成功后取出objectId，赋值给Goods对象，然后保存本地库

    fun addGoods(goods: NewGoods): Observable<Goods> {
        return remote.addGoods(goods).toObservable()
            .map {
                if (!it.isSuccess()) {
                    throw ApiException(it.code)
                }
                val newGoods = Goods(
                    objectId = it.objectId!!,
                    goodsName = goods.goodsName,
                    unitOfMeasurement = goods.unitOfMeasurement,
                    categoryCode = goods.categoryCode,
                    unitPrice = goods.unitPrice
                )
                newGoods
            }

    }


    //2、增加商品类别到远程数据库，传入类别名称，成功后得到新类别的objectId,使用objectId,创建新类别，然后保存本地库

    fun addGoodsCategory(category: NewCategory): Observable<GoodsCategory> {
        return remote.addCategory(category).toObservable()
            .map {
                if (!it.isSuccess()) {
                    throw ApiException(it.code)
                }
                val newCategory = GoodsCategory(it.objectId!!, categoryName = category.categoryName)
                newCategory
            }
    }

    /*
      将全部商品加入到本地
     */
    fun addGoodsListToLocal(list: MutableList<Goods>): Completable {
        return local.addGoodsAll(list)
    }

    fun insertGoodsToLocal(goods: Goods): Completable {
        return local.insertNewGoodsToLocal(goods)
    }


    /*
      将所有商品类别加入本地
     */
    fun addCategoryListToLocal(list: MutableList<GoodsCategory>): Completable {
        return local.addCategoryAll(list)
    }

    fun insertCategoryToLocal(category: GoodsCategory): Completable {
        return local.insertCategoryToLocal(category)
    }


    /*
     *获取购物车内商品数量
     */
    suspend fun getNumberOfMaterialOfShoppingCar(): Int = local.getNumberOfMaterialOfShoppingCar()


    /**
     * 更新
     */
    //1、更新远程数据
    fun updateGoods(goods: Goods): Completable {
        val updateGoods = NewGoods(
            goodsName = goods.goodsName,
            unitOfMeasurement = goods.unitOfMeasurement,
            categoryCode = goods.categoryCode,
            unitPrice = goods.unitPrice
        )
        return remote.updateGoods(updateGoods, goods.objectId)

    }

    //2、更新类别
    fun updateCategory(category: GoodsCategory): Completable {
        val updateCategory = NewCategory(
            categoryName = category.categoryName
        )
        return remote.updateCategory(updateCategory, category.objectId)
    }

    fun getAllCategoryFromNetwork(): Observable<MutableList<GoodsCategory>> {
        return remote.getAllCategory()
    }

    fun getAllGoodsOfCategoryFromNetwork(category: GoodsCategory): Observable<MutableList<Goods>> {
        return remote.getGoodsOfCategory(category)
    }


    /**
     * 使用Flow方式从本地获取数据
     */

    val categorysFlow = local.getAllCategoryFlow()


    fun getGoodsOfCategoryFromLocalFlow(categoryId: String): Flow<List<Goods>> {
        return local.getGoodsOfCategoryFlow(categoryId)
    }

    fun getCookBookWithGoods(objectId: String): CookBookWithGoods {
        return local.getCookBookWithGoods(objectId)
    }

    /*
      根据名字模糊查找商品
     */
    fun findByName(name: String): Observable<MutableList<Goods>> {
        return local.findByName(name)
    }

    /**
     * 删除
     */
    fun deleteGoodsFromLocal(goods: Goods): Completable {
        return local.deleteGoodsFromLocal(goods)
    }

    fun deleteCategoryFromLocal(categroy: GoodsCategory): Completable {
        return local.deleteCategoryFromLocal(categroy)
    }

    fun deleteGoodsFromRemote(goods: Goods): Completable {
        return remote.deleteGoods(goods)
    }

    fun deleteCategoryFromRemote(category: GoodsCategory): Completable {
        return remote.deleteCategory(category)
    }


    /**
     * 清空本地内容，主要是为了同步做准备。
     */
    fun clearAllData(): Completable {
        return local.clearGoodsAll().andThen(local.clearCategoryAll())
    }

    /**
     * 获取某日菜单
     */
    suspend fun getDailyMealOfDate(where: String): ObjectList<DailyMeal> {
        return remote.getDailyMealOfDate(where)
    }

    /**
     * 新版本对购物车的操作
     */
    suspend fun addFoodAndMaterialsToShoppingCar(
        food: FoodOfShoppingCar,
        materialList: List<MaterialOfShoppingCar>
    ) {
        local.addFoodAndMaterial(food, materialList)
    }


}