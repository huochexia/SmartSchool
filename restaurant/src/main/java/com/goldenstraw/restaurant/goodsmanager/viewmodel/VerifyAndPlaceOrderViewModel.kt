package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.lifecycle.MutableLiveData
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrderItem
import com.goldenstraw.restaurant.goodsmanager.http.entities.BatchOrdersRequest
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.User
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class VerifyAndPlaceOrderViewModel(
    private val repository: VerifyAndPlaceOrderRepository
) : BaseViewModel() {

    val suppliers = mutableListOf<User>() //供应商列表
    val isPopUpSupplierDialog = MutableLiveData<Boolean>()//观察是否弹出选择供应列表对话框
    val isRefresh = MutableLiveData<Boolean>() //Fragment用于观察是否刷新列表
    var selectedSupplier = ""

    init {
        getAllSupplier()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({
                suppliers.clear()
                suppliers.addAll(it)
            }, {}, {})
    }

    /**
     * 获取拟购单
     */
    fun getAllOrderOfDate(date: String): Observable<MutableList<OrderItem>> {
        return repository.getAllOrderOfDate(date)
    }

    /**
     * 获取所有供应商
     */
    fun getAllSupplier(): Observable<MutableList<User>> {
        return repository.getAllSupplier()
    }

    /**
     * 将订单转换成批量处理请求对象.先将每一个订单转换成BatchAddOrderItem对象，
     * 然后以40个为一组，组成一个列表，最后将这个列表赋值给BatchOrdersRequest
     */
    fun transOrdersToBatchRequestObject(
        list: MutableList<OrderItem>,
        supplier: String
    ): Observable<BatchOrdersRequest<ObjectSupplier>> {
        return Observable.fromIterable(list)
            .map {
                val updateSupplier = ObjectSupplier(supplier, state = 1)
                val batchItem = BatchOrderItem(
                    method = "PUT",
                    path = "/1/classes/OrderItem/${it.objectId}",
                    body = updateSupplier
                )
                batchItem
            }
            .buffer(40)
            .map {
                val batch = BatchOrdersRequest(requests = it)
                batch
            }
    }

    /**
     * 发送订单给供应商
     */
    fun sendToOrderToSupplier(orders: BatchOrdersRequest<ObjectSupplier>): Completable {
        return repository.sendOrdersToSupplier(orders)
    }

}