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
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ShoppingCartMgViewModel(
    private val repository: ShoppingCartRepository
) : BaseViewModel() {

    val goodsList = mutableListOf<GoodsOfShoppingCart>()
    val state = ObservableField<Int>()

    init {
        getAllGoodsOfShoppingCart()
    }

    /**
     * 获取购物车中的商品信息
     */
    private fun getAllGoodsOfShoppingCart() {
        repository.getAllShoppingCart().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({
                if (it.isNotEmpty()) {
                    state.set(MultiStateView.VIEW_STATE_CONTENT)
                    goodsList.clear()
                    goodsList.addAll(it)
                }

            }, {
                state.set(MultiStateView.VIEW_STATE_ERROR)
            }, {

            }, {
                state.set(MultiStateView.VIEW_STATE_LOADING)
            })
    }

    /**
     * 删除购物车商品
     */
    fun deleteGoodsOfShoppingCartList(list: MutableList<GoodsOfShoppingCart>): Completable {
        return repository.deleteGoodsOfShoppingCartListFromLocal(list)
    }

    fun deleteGoodsOfShoppingCart(goods: GoodsOfShoppingCart) {
        repository.deleteGoodsOfShoppingCartFromLocal(goods)
            .subscribeOn(Schedulers.computation())
            .autoDisposable(this)
            .subscribe({}, {})
    }

    /**
     * 修改购物画商品
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