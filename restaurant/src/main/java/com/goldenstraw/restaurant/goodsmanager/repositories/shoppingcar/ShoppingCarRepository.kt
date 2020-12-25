package com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcar

import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.room.entities.FoodWithMaterialsOfShoppingCar
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.MaterialOfShoppingCar
import com.owner.basemodule.room.entities.NewOrder

import io.reactivex.Completable

class ShoppingCarRepository(
    private val remote: IRemoteShoppingCarDataSource,
    private val local: ILocalShoppingCarDataSource
) : BaseRepositoryBoth<IRemoteShoppingCarDataSource, ILocalShoppingCarDataSource>(remote, local) {

    /*
     将本地购物车中商品提交成订单
     */

    fun createNewOrderItem(orderItem: BatchOrdersRequest<NewOrder>): Completable {

        return remote.createNewOrderItem(orderItem)
    }

    /**
     * 新版本购物车操作方法,获取，修改，删除。
     */
    suspend fun getFoodOfShoppingCar(): List<FoodWithMaterialsOfShoppingCar> {
        return local.getFoodOfShoppingCar()
    }

    suspend fun getMaterialOfShoppingCar(id: String): MaterialOfShoppingCar {
        return local.getMaterialOfShoppingCar(id)
    }

    suspend fun getAllOfMaterialShoppingCar(): List<MaterialOfShoppingCar> {
        return local.getAllOfMaterialOfShoppingCar()
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

    suspend fun deleteMaterialOfShoppingCar(material: MaterialOfShoppingCar) {
        local.deleteMaterialOfShoppingCar(material)
    }

    suspend fun createNewOrder(list: List<NewOrder>) {
        local.createNewOrder(list)
    }


    suspend fun getLocalNewOrder(): List<NewOrder> {
        return local.getLocalNewOrder()
    }

    suspend fun getPriceOfGoods(goodsId: String): Goods {
        return local.getPriceOfGoods(goodsId)
    }

    suspend fun updateLocalNewOrder(newOrder: NewOrder) {
        return local.updateLocalNewOrder(newOrder)
    }

    suspend fun clearLocalNewOrder() {
        return local.clearAllNewOrder()
    }

    suspend fun deleteLocalNewOrder(newOrder: NewOrder) {
        return local.deleteLocalNewOrder(newOrder)
    }
}