package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrderItem
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewOrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart.ShoppingCartRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import com.owner.basemodule.util.TimeConverter
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.Observable

class ShoppingCartMgViewModel(
    private val repository: ShoppingCartRepository
) : BaseViewModel() {

    var goodsList = mutableListOf<GoodsOfShoppingCart>()
    val state = ObservableField<Int>()

//    init {
////        getAllGoodsOfShoppingCart()
//        getAllGoodsOfShoppingCart(CookKind.HotFood.kindName)
//    }

    /**
     * 获取购物车中的商品信息
     */
    fun getAllGoodsOfShoppingCart(foodCategory: String) {
        launchUI {
            val list = repository.getGoodsOfShoppingCart(foodCategory)
            if (list.isNotEmpty()) {
                state.set(MultiStateView.VIEW_STATE_CONTENT)
                goodsList = list
                goodsList.sortBy {
                    it.categoryCode
                }
                defUI.refreshEvent.call()
            } else {
                state.set(MultiStateView.VIEW_STATE_EMPTY)
            }

        }
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
                .autoDisposable(this@ShoppingCartMgViewModel)
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

    /**
     * 删除购物车商品
     */
    fun deleteGoodsOfShoppingCartList(list: MutableList<GoodsOfShoppingCart>): Completable {
        return repository.deleteGoodsOfShoppingCartListFromLocal(list)
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
     * 修改购物车商品
     */
    fun updateGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable {
        return repository.updateGoodsOfShoppingCart(goods)
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