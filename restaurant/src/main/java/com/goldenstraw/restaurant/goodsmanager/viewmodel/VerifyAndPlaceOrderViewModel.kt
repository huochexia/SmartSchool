package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.network.parserResponse
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

class VerifyAndPlaceOrderViewModel(
    private val repository: VerifyAndPlaceOrderRepository
) : BaseViewModel() {

    var suppliers = mutableListOf<User>() //供应商列表

    var ordersList = mutableListOf<OrderItem>()//订单列表

    var new = MutableLiveData<String>()


    var viewState = ObservableField<Int>()

    init {
        getAllSupplier()

    }

    /**
     * 获取某个条件订单
     */
    fun getOrdersOfDate(condition: String) {
        launchUI {
            parserResponse(repository.getOrdersOfDate(condition)) {
                if (it.isEmpty()) {
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                    ordersList = it
                }
                defUI.refreshEvent.call()
            }
        }
        return
    }

    /**
     * 获取所有供应商
     */
    fun getAllSupplier() {
        launchUI {
            val where = "{\"role\":\"供应商\"}"
            parserResponse(repository.getSupplier(where)){
                suppliers = it
            }
        }
        return
    }

    /**
     * 删除订单
     */
    fun deleteOrderItem(objectId: String) {
        launchUI {
            parserResponse(repository.deleteOrderItem(objectId))
        }
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
     * 修改订单数量
     */
    fun updateOrderItem(order: OrderItem) {
        launchUI {
            val newQuantity = ObjectQuantityAndNote(order.quantity,order.note)
            parserResponse(repository.updateOrderItem(newQuantity, order.objectId))
        }
    }

    /**
     * 单个验货
     */
    fun setCheckQuantity(newQuantity: ObjectCheckGoods, orders: OrderItem) {
        launchUI {
            parserResponse(repository.setCheckQuantity(newQuantity, orders.objectId)) {
                ordersList.remove(orders)
                defUI.refreshEvent.call()
            }
        }
    }

    /**
     * 批量验货
     */
    fun checkQuantityOfOrders(orders: BatchOrdersRequest<ObjectState>): Completable {
        return repository.checkQuantityOfOrders(orders)
    }

    /**
     * 提交记帐
     */
    fun commitRecordState(orders: BatchOrdersRequest<ObjectState>): Completable {
        return repository.commitRecordState(orders)
    }

//    /**
//     * 得到所有数据并计算
//     */
//    fun computeNewAndOldOfDiffer(supplier: String, start: String, end: String) {
//        val where =
//            "{\"\$and\":[{\"supplier\":\"$supplier\"},{\"orderDate\":{\"\$gte\":\"$start\",\"\$lte\":\"$end\"}}]}"
//        val formate = DecimalFormat("0")
//        var newprice = 0.0f
//        var oldprice = 0.0f
//        getAllOrderOfDate(where)
//            .subscribeOn(Schedulers.computation())
//            .flatMap {
//                Observable.fromIterable(it)
//            }
//            .map {
//                val map = HashMap<Int, Float>()
//                oldprice += it.total
//                newprice += it.unitPrice * it.againCheckQuantity
//                map[0] = newprice
//                map[1] = oldprice
//                map
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .autoDisposable(this)
//            .subscribe({
//                new.value = formate.format(it[0])
//                old.value = formate.format(it[1])
//                differ.value = formate.format(it[0]?.minus(it[1]!!))
//            }, {}, {
//
//            })

//    }
}