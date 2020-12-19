package com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcar

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewOrderItem
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.room.entities.FoodWithMaterialsOfShoppingCar
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import com.owner.basemodule.room.entities.MaterialOfShoppingCar
import io.reactivex.Completable

class ShoppingCarRepository(
    private val remote: IRemoteShoppingCarDataSource,
    private val local: ILocalShoppingCarDataSource
) : BaseRepositoryBoth<IRemoteShoppingCarDataSource, ILocalShoppingCarDataSource>(remote, local) {
    /*
      获取购物车内所有商品
     */
    suspend fun getAllOfShoppingCart(): MutableList<GoodsOfShoppingCart> {

        return local.getAllGoodsOfFoodCategory()
    }

    fun deleteGoodsOfShoppingCartFromLocal(goods: GoodsOfShoppingCart): Completable {
        return local.deleteGoodsOfShoppingCart(goods)
    }

    fun deleteGoodsOfShoppingCartListFromLocal(list: MutableList<GoodsOfShoppingCart>): Completable {
        return local.deleteShoppingCartList(list)
    }

    fun deleteAllOfShoppingCart(): Completable {
        return local.deleteAllShoppingCart()
    }

    fun updateGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable {
        return local.updateGoodsOfShoppingCart(goods)
    }

    suspend fun getGoodsOfShoppingCart(foodCategory: String): MutableList<GoodsOfShoppingCart> {
        return local.getGoodsOfFoodCategory(foodCategory)
    }
    /*
     将本地购物车中商品提交成订单
     */

    fun createNewOrderItem(orderItem: BatchOrdersRequest<NewOrderItem>): Completable {

        return remote.createNewOrderItem(orderItem)
    }

    /**
     * 新版本购物车操作方法,获取，修改，删除。
     */
    suspend fun getFoodOfShoppingCar(): List<FoodWithMaterialsOfShoppingCar> {
        return local.getFoodOfShoppingCar()
    }

    suspend fun getMaterialOfShopping(id: String): List<MaterialOfShoppingCar> {
        return local.getMaterialOfShoppingCar(id)
    }

    suspend fun batchUpdateQuantityOfMaterial(list: List<MaterialOfShoppingCar>) {
        local.batchQuantityOfMaterial(list)
    }

    suspend fun updateQuantityOfMaterial(material: MaterialOfShoppingCar) {
        local.updateQuantityOfMaterial(material)
    }

    suspend fun clearFoodOfShoppingCar() {
        local.clearFoodOfShoppingCar()
    }

    suspend fun clearMaterialOfShoppingCar() {
        local.clearMaterialOfShoppingCar()
    }

    suspend fun deleteMaterialOfShoppingCar(material: MaterialOfShoppingCar){
        local.deleteMaterialOfShoppingCar(material)
    }
}