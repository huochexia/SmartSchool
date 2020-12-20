package com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcar

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.entities.*
import io.reactivex.Completable

interface ILocalShoppingCarDataSource : ILocalDataSource {

    suspend fun getAllGoodsOfFoodCategory(): MutableList<GoodsOfShoppingCart>

    fun deleteShoppingCartList(goodslist: MutableList<GoodsOfShoppingCart>): Completable

    fun deleteGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable

    fun updateGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable

    fun deleteAllShoppingCart(): Completable

    suspend fun getGoodsOfFoodCategory(foodCategory: String): MutableList<GoodsOfShoppingCart>

    /**
     * 新版本购物车部分
     * 在购物车中针对数据库的业务操作主要是获取食物和原材料，获取某类比如通用的原材料，
     * 保存原材料的用量，删除食品和原材料
     **/

    /*
      删除
     */
    suspend fun clearFoodOfShoppingCar()

    suspend fun clearMaterialOfShoppingCar()

    suspend fun deleteMaterialOfShoppingCar(material: MaterialOfShoppingCar)

    /*
      获取
     */
    suspend fun getFoodOfShoppingCar(): List<FoodWithMaterialsOfShoppingCar>

    suspend fun getMaterialOfShoppingCar(id: String): MaterialOfShoppingCar

    suspend fun getAllOfMaterialOfShoppingCar(): List<MaterialOfShoppingCar>

    /*
    修改
     */
    suspend fun batchQuantityOfMaterial(list: List<MaterialOfShoppingCar>)

    suspend fun updateQuantityOfMaterial(material: MaterialOfShoppingCar)

    /*
    将原材料转换成订单
     */

    suspend fun createNewOrder(list: List<NewOrder>)

    suspend fun getLocalNewOrder(): List<NewOrder>

    suspend fun updateLocalNewOrder(newOrder: NewOrder)

    suspend fun clearAllNewOrder()

    suspend fun deleteLocalNewOrder(newOrder: NewOrder)
    /*
    获取订单对应商品
     */
    suspend fun getPriceOfGoods(goodsId:String):Goods
}

/**
 * 管理本地购物车
 */
class LocalShoppingCartDataSourceImpl(
    private val database: AppDatabase
) : ILocalShoppingCarDataSource {
    /*
     * 获取购物车内所有商品
     */
    override suspend fun getAllGoodsOfFoodCategory(): MutableList<GoodsOfShoppingCart>{
        return database.goodsDao().getAllShoppingCart()
    }

    override fun deleteShoppingCartList(goodslist: MutableList<GoodsOfShoppingCart>): Completable {
        return database.goodsDao().deleteShoppingCartList(goodslist)
    }

    override fun deleteGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable {
        return database.goodsDao().deleteGoodsOfShoppingCart(goods)
    }

    override fun deleteAllShoppingCart(): Completable {
        return database.goodsDao().delteAllShoppingCart()
    }

    override fun updateGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable {
        return database.goodsDao().insertShoppingCart(goods)
    }

    override suspend fun getGoodsOfFoodCategory(foodCategory: String): MutableList<GoodsOfShoppingCart> {
        return database.goodsDao().getShoppingCartOfFoodCategory(foodCategory)
    }


    override suspend fun clearFoodOfShoppingCar() {
        database.shoppingCarDao().clearFoods()
    }

    override suspend fun clearMaterialOfShoppingCar() {
        database.shoppingCarDao().clearMaterials()
    }

    override suspend fun deleteMaterialOfShoppingCar(material: MaterialOfShoppingCar) {
        database.shoppingCarDao().deleteMaterial(material)
    }

    override suspend fun getFoodOfShoppingCar(): List<FoodWithMaterialsOfShoppingCar> {
        return database.shoppingCarDao().getFoodAndMaterial()
    }

    override suspend fun getMaterialOfShoppingCar(id: String): MaterialOfShoppingCar {
        return database.shoppingCarDao().getMaterialOfShopping(id)
    }

    override suspend fun getAllOfMaterialOfShoppingCar(): List<MaterialOfShoppingCar> {
        return database.shoppingCarDao().getAllOfMaterialOfShoppingCar()
    }

    override suspend fun batchQuantityOfMaterial(list: List<MaterialOfShoppingCar>) {
        database.shoppingCarDao().batchUpdateQuantityOfMaterial(list)
    }

    override suspend fun updateQuantityOfMaterial(material: MaterialOfShoppingCar) {
        database.shoppingCarDao().updateQuantityOfMaterial(material)
    }


    override suspend fun createNewOrder(list: List<NewOrder>) {
        database.orderDao().insertNewOrder(list)
    }

    override suspend fun getLocalNewOrder(): List<NewOrder> {
        return database.orderDao().getNewOrder()
    }

    override suspend fun getPriceOfGoods(goodsId: String): Goods {
        return database.goodsDao().getGoodsFromObjectId(goodsId)
    }
    override suspend fun updateLocalNewOrder(newOrder: NewOrder) {
        database.orderDao().updateNewOrder(newOrder)
    }

    override suspend fun clearAllNewOrder() {
        database.orderDao().clearNewOrder()
    }

    override suspend fun deleteLocalNewOrder(newOrder: NewOrder) {
        database.orderDao().deleteNewOrder(newOrder)
    }

}