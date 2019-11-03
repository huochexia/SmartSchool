package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.lifecycle.MutableLiveData
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.User
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.DecimalFormat

class VerifyAndPlaceOrderViewModel(
    private val repository: VerifyAndPlaceOrderRepository
) : BaseViewModel() {

    val suppliers = mutableListOf<User>() //供应商列表

    var new = MutableLiveData<String>()
    var old = MutableLiveData<String>()
    var differ = MutableLiveData<String>()

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
    fun getAllOrderOfDate(condition: String): Observable<MutableList<OrderItem>> {
        return repository.getAllOrderOfDate(condition)
    }

    /**
     * 获取所有供应商
     */
    fun getAllSupplier(): Observable<MutableList<User>> {
        return repository.getAllSupplier()
    }

    /**
     * 删除订单
     */
    fun deleteOrderItem(objectId: String): Completable {
        return repository.deleteOrderItem(objectId)
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

    /**
     * 推送通知
     */

    fun pushNotice(installactionId: String, notice: String): Completable {
        return repository.pushNotice(installactionId, notice)
    }

    /**
     * 修改订单数量
     */
    fun updateOrderItemQuantity(order: OrderItem) {
        val newQuantity = ObjectQuantity(order.quantity)
        repository.updateOrderItemQuantity(newQuantity, order.objectId)
            .subscribeOn(Schedulers.io())
            .autoDisposable(this)
            .subscribe({}, {})
    }

    /**
     * 单个验货
     */
    fun setCheckQuantity(newQuantity: ObjectCheckGoods, objectId: String): Completable {

        return repository.setCheckQuantity(newQuantity, objectId)
    }

    /**
     * 批量验货
     */
    fun checkQuantityOfOrders(orders: BatchOrdersRequest<ObjectCheckGoods>): Completable {
        return repository.checkQuantityOfOrders(orders)
    }

    /**
     * 提交记帐
     */
    fun commitRecordState(orders: BatchOrdersRequest<ObjectState>): Completable {
        return repository.commitRecordState(orders)
    }

    /**
     * 得到所有数据并计算
     */
    fun computeNewAndOldOfDiffer(supplier: String, start: String, end: String) {
        val where =
            "{\"\$and\":[{\"supplier\":\"$supplier\"},{\"orderDate\":{\"\$gte\":\"$start\",\"\$lte\":\"$end\"}}]}"
        val formate = DecimalFormat("0")
        var newprice = 0.0f
        var oldprice = 0.0f
        getAllOrderOfDate(where)
            .subscribeOn(Schedulers.computation())
            .flatMap {
                Observable.fromIterable(it)
            }
            .map {
                val map = HashMap<Int, Float>()
                oldprice += it.total
                newprice += it.unitPrice * it.againCheckQuantity
                map[0] = newprice
                map[1] = oldprice
                map
            }
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({
                new.value = formate.format(it[0])
                old.value = formate.format(it[1])
                differ.value = formate.format(it[0]?.minus(it[1]!!))
            }, {}, {

            })

    }
}