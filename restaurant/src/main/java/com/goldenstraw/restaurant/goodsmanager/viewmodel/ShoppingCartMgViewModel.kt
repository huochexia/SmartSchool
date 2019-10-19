package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewOrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart.ShoppingCartRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
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
        repository.local.getAllGoods().subscribeOn(Schedulers.io())
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
        return repository.local.deleteShoppingCartList(list)
    }

    fun deleteGoodsOfShoppingCart(goods: GoodsOfShoppingCart) {
        repository.local.deleteGoodsOfShoppingCart(goods)
            .subscribeOn(Schedulers.computation())
            .autoDisposable(this)
            .subscribe({}, {})
    }

    /**
     * 修改购物画商品
     */
    fun updateGoodsOfShoppingCart(goods: GoodsOfShoppingCart): Completable {
        return repository.local.updateGoodsOfShoppingCart(goods)
    }

    /**
     * 将购物车内商品信息提交网络
     */
    fun commitGoodsOfShoppingCart(orderItem: NewOrderItem): Completable {
        return repository.remote.insertGoodsOfShoppingCart(orderItem)
    }

}