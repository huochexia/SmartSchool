package com.goldenstraw.restaurant.goodsmanager.viewmodel

import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.User
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class QueryOrdersViewModel(
    private val repository: QueryOrdersRepository
) : BaseViewModel() {

    val suppliers = mutableListOf<User>() //供应商列表

    init {
        getAllSupplier()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({
                suppliers.clear()
                suppliers.addAll(it)
            }, {}, {

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

    fun getOrdersOfSupplier(supplier: String, date: String): Observable<MutableList<OrderItem>> {
        val where = "{\"\$and\":[{\"supplier\":\"$supplier\"},{\"orderDate\":\"$date\"}]}"
        return repository.getOrdersOfSupplier(where)
    }

    /**
     *
     */
    fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable {
        return repository.updateOrderOfSupplier(newOrder, objectId)
    }


}