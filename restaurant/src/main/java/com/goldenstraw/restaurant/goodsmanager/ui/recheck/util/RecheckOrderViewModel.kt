package com.goldenstraw.restaurant.goodsmanager.ui.recheck.util

import androidx.lifecycle.MutableLiveData
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrderItem
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.User
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.DecimalFormat

class RecheckOrderViewModel(
    private val repository: RecheckOrderRepository
) : BaseViewModel() {

    val suppliers = mutableListOf<User>() //供应商列表

    var new = MutableLiveData<String>()


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
     * 获取某个条件订单
     */
    fun getAllOrderOfCondition(condition: String): Observable<MutableList<OrderItem>> {
        return repository.getAllOrderOfDate(condition)
    }

    /**
     * 获取所有供应商
     */
    fun getAllSupplier(): Observable<MutableList<User>> {
        return repository.getAllSupplier()
    }


    /**
     *
     */
    fun transOrdersToBatchRequestObject(
        list: MutableList<OrderItem>

    ): Observable<BatchOrdersRequest<BatchRecheckObject>> {
        return Observable.fromIterable(list)
            .map {
                val format = DecimalFormat(".00")
                val newtotal = format.format(it.againCheckQuantity * it.unitPrice).toFloat()
                val update = BatchRecheckObject(
                    quantity = it.reQuantity,
                    checkQuantity = it.againCheckQuantity,
                    total = newtotal,
                    againTotal = newtotal,
                    state = 3
                )
                val batchItem = BatchOrderItem(
                    method = "PUT",
                    path = "/1/classes/OrderItem/${it.objectId}",
                    body = update
                )
                batchItem
            }
            .buffer(50)
            .map {
                val batch = BatchOrdersRequest(requests = it)
                batch
            }
    }


    /**
     * 修改订单数量
     */
    fun updateOrderItemQuantity(newQuantity: ObjectReCheck, objectId: String):Completable {
        return repository.updateOrderItemQuantity(newQuantity, objectId)

    }


    /**
     * 批量验货
     */
    fun checkQuantityOfOrders(orders: BatchOrdersRequest<BatchRecheckObject>): Completable {
        return repository.checkQuantityOfOrders(orders)
    }

    /**
     * 分组求和
     */
    fun getTotalOfSuppliers(condition: String): Observable<MutableList<SupplierOfTotal>> {
        return repository.getTotalOfSuppliers(condition)
    }

}