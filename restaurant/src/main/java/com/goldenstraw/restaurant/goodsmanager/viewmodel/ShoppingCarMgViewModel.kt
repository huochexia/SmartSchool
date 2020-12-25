package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrderItem
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcar.ShoppingCarRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.*
import com.owner.basemodule.util.TimeConverter
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShoppingCarMgViewModel(
    private val repository: ShoppingCarRepository
) : BaseViewModel() {


    val state = ObservableField<Int>()

    var foodList = mutableListOf<FoodWithMaterialsOfShoppingCar>()

    var newOrderList = mutableListOf<NewOrder>()

    var categoryList = mutableListOf<FoodWithMaterialsOfShoppingCar>()

    init {
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
            val allList = repository.getAllOfMaterialShoppingCar()
            val collect = hashMapOf<String, MaterialOfShoppingCar>()
            Observable.fromIterable(allList)
                .filter {
                    !it.quantity.equals(0.0f)
                }
                .autoDisposable(this@ShoppingCarMgViewModel)
                .subscribe({
                    if (collect.contains(it.goodsName)) {
                        collect[it.goodsName]!!.quantity =
                            (collect[it.goodsName]!!.quantity + it.quantity)

                    } else {
                        collect[it.goodsName] = it
                    }
                }, {}, {
                    collectAllMaterialOfShoppingCar(collect.values.toMutableList())
                })

        }
    }

    /**
     * 将汇总去重后的食物材料
     */
    private fun collectAllMaterialOfShoppingCar(list: List<MaterialOfShoppingCar>) {
        categoryList.clear()
        val food = FoodOfShoppingCar("collect", "共${list.size}种商品", "", "")
        list.forEach {
            it.materialOwnerId = "collect"
        }
        categoryList.add(FoodWithMaterialsOfShoppingCar(food, list))

        defUI.refreshEvent.call()
    }

    /**
     * 生成订单
     */
    fun createNewOrder(list: List<NewOrder>) {
        launchUI {
            repository.createNewOrder(list)
            repository.clearFoodOfShoppingCar()
            repository.clearMaterialOfShoppingCar()
            categoryList.clear()
            defUI.refreshEvent.call()
        }
    }

    /**
     * 修改订单
     */
    fun updateNewOrder(newOrder: NewOrder) {
        launchUI {
            repository.updateLocalNewOrder(newOrder)
            defUI.refreshEvent.call()
        }
    }

    /**
     * 删除订单
     */
    fun deleteNewOrder(newOrder: NewOrder) {
        launchUI {
            repository.deleteLocalNewOrder(newOrder)
            newOrderList.remove(newOrder)
            defUI.refreshEvent.call()
        }
    }

    /**
     * 清空本地订单
     */
    fun clearAllNewOrder() {
        launchUI {
            repository.clearLocalNewOrder()
            withContext(Dispatchers.Main) {
                newOrderList.clear()
                defUI.refreshEvent.call()
            }

        }
    }

    /**
     * 获取本地保存的新订单
     */
    fun getLocalNewOrder() {
        launchUI {
            newOrderList = repository.getLocalNewOrder() as MutableList<NewOrder>
            withContext(Dispatchers.Default) {
                newOrderList.forEach { order ->
                    val goods = repository.getPriceOfGoods(order.goodsId)
                    //因为从商品得到原材料后，商品有可能被删除。而原材料中依然保留商品Id
                    goods?.let {
                        order.unitPrice = it.unitPrice
                    }
                }
                createNewOrder(newOrderList)
            }
            defUI.refreshEvent.call()
        }
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

    fun commitNewOrderToRemote() {
        launchUI {
            withContext(Dispatchers.IO) {
                transGoodsOfShoppingCartToNewOrderItem(newOrderList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .autoDisposable(this@ShoppingCarMgViewModel)
                    .subscribe({
                        createNewOrderItem(it)
                            .subscribeOn(Schedulers.io())
                            .autoDisposable(this@ShoppingCarMgViewModel)
                            .subscribe()
                    }, {}, {
                        clearAllNewOrder()
                    })
            }

        }

    }

    /**
     * 转换
     */
    private fun transGoodsOfShoppingCartToNewOrderItem(
        list: MutableList<NewOrder>
    ): Observable<BatchOrdersRequest<NewOrder>> {
        return Observable.fromIterable(list)
            .map { order ->
                order.orderDate = TimeConverter.getCurrentDateString()
                val batchItem = BatchOrderItem(
                    method = "POST",
                    path = "/1/classes/OrderItem/",
                    body = order
                )
                batchItem
            }
            .buffer(45)
            .map {
                val batch = BatchOrdersRequest(requests = it)
                batch
            }
    }

    /**
     * 将购物车内商品信息提交网络
     */
    private fun createNewOrderItem(orderItem: BatchOrdersRequest<NewOrder>): Completable {
        return repository.createNewOrderItem(orderItem)
    }

}