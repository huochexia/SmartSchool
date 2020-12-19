package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrderItem
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewOrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcar.ShoppingCarRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.FoodWithMaterialsOfShoppingCar
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import com.owner.basemodule.room.entities.MaterialOfShoppingCar
import com.owner.basemodule.util.TimeConverter
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.Observable

class ShoppingCarMgViewModel(
    private val repository: ShoppingCarRepository
) : BaseViewModel() {

    var goodsList = mutableListOf<GoodsOfShoppingCart>()
    val state = ObservableField<Int>()
    var foodList = mutableListOf<FoodWithMaterialsOfShoppingCar>()

    var categoryList = mutableListOf<FoodWithMaterialsOfShoppingCar>()

    init {
//        getAllGoodsOfShoppingCart()
//        getAllGoodsOfShoppingCart(CookKind.HotFood.kindName)
        launchUI {
            foodList =
                repository.getFoodOfShoppingCar() as MutableList<FoodWithMaterialsOfShoppingCar>
            categoryList.addAll(foodList)
            if (categoryList.isNullOrEmpty()) {
                state.set(MultiStateView.VIEW_STATE_EMPTY)
            } else {
                state.set(MultiStateView.VIEW_STATE_CONTENT)
            }
            defUI.refreshEvent.call()
        }
    }

    /**
     * 新版本分类购物车食品和原材料
     */
    fun groupByFoodCategory(category: String) {
        categoryList.clear()
        categoryList.addAll(foodList.filter {
            it.food.foodCategory == category
        })
        categoryList.sortBy { it.food.foodTime }
        if (categoryList.isNullOrEmpty()) {
            state.set(MultiStateView.VIEW_STATE_EMPTY)
        } else {
            state.set(MultiStateView.VIEW_STATE_CONTENT)
        }
        defUI.refreshEvent.call()
    }


    /**
     * 将汇总所有拟购商品，合计重复商品数量，商品数量为0的删除
     */
    fun collectAllOfFoodCategory() {
        launchUI {
            val allList = repository.getAllOfShoppingCart()
            val collect = hashMapOf<String, GoodsOfShoppingCart>()
            Observable.fromIterable(allList)
                .filter {
                    !it.quantity.equals(0.0f)
                }
                .autoDisposable(this@ShoppingCarMgViewModel)
                .subscribe({
                    if (collect.contains(it.goodsName)) {
                        collect[it.goodsName]!!.quantity =
                            collect[it.goodsName]!!.quantity + it.quantity

                    } else {
                        collect[it.goodsName] = it
                    }
                }, {}, {
                    goodsList = collect.values.toMutableList()
                    goodsList.sortBy {
                        it.categoryCode
                    }
                    defUI.refreshEvent.call()
                })

        }
    }


    fun deleteGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable {
        return repository.deleteGoodsOfShoppingCartFromLocal(goods)

    }

    fun deleteAllOfShoppingCart(): Completable {
        return repository.deleteAllOfShoppingCart()
    }

    /**
     * 新版本清空购物车
     */
    fun clearShopping() {
        launchUI {
            repository.clearFoodOfShoppingCar()
            repository.clearMaterialOfShoppingCar()
        }
    }

    /**
     * 删除购物车商品
     */
    fun deleteMaterialOfShoppingCar(material: MaterialOfShoppingCar) {
        launchUI {
            repository.deleteMaterialOfShoppingCar(material)
            categoryList.forEach {
                (it.materials as MutableList).remove(material)
            }
        }
    }

    /**
     * 修改购物车商品
     */
    fun updateMaterialOfShoppingCar(material: MaterialOfShoppingCar) {
        launchUI {
            repository.updateQuantityOfMaterial(material)
        }
    }

    /**
     * 转换
     */
    fun transGoodsOfShoppingCartToNewOrderItem(
        list: MutableList<GoodsOfShoppingCart>,
        dist: Int
    ): Observable<BatchOrdersRequest<NewOrderItem>> {
        return Observable.fromIterable(list)
            .map {
                val order = NewOrderItem(
                    district = dist,
                    goodsName = it.goodsName,
                    unitOfMeasurement = it.unitOfMeasurement,
                    unitPrice = it.unitPrice,
                    quantity = it.quantity,
                    note = it.note,
                    categoryCode = it.categoryCode,
                    orderDate = TimeConverter.getCurrentDateString(),
                    state = 0
                )
                val batchItem = BatchOrderItem<NewOrderItem>(
                    method = "POST",
                    path = "/1/classes/OrderItem/",
                    body = order
                )
                batchItem
            }
            .buffer(45)
            .map {
                var batch = BatchOrdersRequest<NewOrderItem>(requests = it)
                batch
            }
    }

    /**
     * 将购物车内商品信息提交网络
     */
    fun createNewOrderItem(orderItem: BatchOrdersRequest<NewOrderItem>): Completable {
        return repository.createNewOrderItem(orderItem)
    }

}