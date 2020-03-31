package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class QueryOrdersViewModel(
    private val repository: QueryOrdersRepository
) : BaseViewModel() {
    //所有供应商列表
    val suppliers = mutableListOf<User>() //供应商列表
    //用于查询某个供应商订单
    var supplier: String = ""

    var viewState = ObservableField<Int>()
    init {
        getAllSupplier()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({
                suppliers.clear()
                suppliers.addAll(it)
                if (suppliers.isEmpty()) {
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                }
            }, {
                viewState.set(MultiStateView.VIEW_STATE_ERROR)
            }, {

            }, {
                viewState.set(MultiStateView.VIEW_STATE_LOADING)
            })
    }

    /**
     * 获取所有供应商
     */
    fun getAllSupplier(): Observable<MutableList<User>> {
        return repository.getAllSupplier()
    }

    /**
     * 按日期获取供应商订单
     */

    fun getOrdersOfSupplier(where: String): Observable<MutableList<OrderItem>> {
        return repository.getOrdersOfSupplier(where)
    }

    /**
     *  发送到供应商
     */
    fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable {
        return repository.updateOrderOfSupplier(newOrder, objectId)
    }

    /**
     * 获取商品信息
     */
    fun getGoodsOfCategory(condition: String): Observable<MutableList<Goods>> {
        return repository.getGoodsOfCategory(condition)
    }

    /**
     * 求和
     */
    fun getTotalOfSupplier(condition: String): Observable<MutableList<SumResult>> {
        return repository.getTotalOfSupplier(condition)
    }
    /**
     * 分组求和
     */
    fun getTotalGroupByName(condition: String): Observable<MutableList<SumByGroup>> {
        return repository.getTotalGroupByName(condition)
    }

    /**
     * 提交新单价
     */
    fun updateNewPriceOfGoods(newPrice: NewPrice,objectId: String): Completable {
        return repository.updateNewPrice(newPrice,objectId)

    }
}