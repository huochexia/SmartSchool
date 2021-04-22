package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.network.ResponseThrowable
import com.owner.basemodule.network.parserResponse
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable

class VerifyAndPlaceOrderViewModel(
    private val repository: VerifyAndPlaceOrderRepository
) : BaseViewModel() {

    var suppliers = mutableListOf<User>() //供应商列表

    /*共享列表。
     不同的Fragment创建自己的列表，从这里获取所需要的数据。对数据的修改要两者兼顾
     */
    var ordersList = mutableListOf<OrderItem>()


    var viewState = ObservableField<Int>()
     //共享变量，主要是为在不同Fragment中统一使用它，来保证前后的一致性。
    var orderState = 1

    /**
     * 获取某个条件订单
     */
    fun getOrdersOfCondition(condition: String) {
        launchUI ({

            parserResponse(repository.getOrdersOfDate(condition)) {
                if (it.isEmpty()) {
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                    ordersList.clear()
                } else {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                    ordersList = it
                }
                defUI.refreshEvent.call()
            }
        },{
            defUI.showDialog.value =  (it as ResponseThrowable).errMsg
        })

    }

    /**
     * 获取所有供应商
     */
    fun getAllSupplier() {
        launchUI( {
            val where = "{\"role\":\"供应商\"}"
            parserResponse(repository.getSupplier(where)){
                suppliers = it
            }
        },{
            defUI.showDialog.value =  (it as ResponseThrowable).errMsg
        })

    }

    /**
     * 删除订单
     */
    fun deleteOrderItem(orders: OrderItem) {
        launchUI({
            parserResponse(repository.deleteOrderItem(orders.objectId)) {
                ordersList.remove(orders)
            }
        }, {
            defUI.showDialog.value = (it as ResponseThrowable).errMsg
        })
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
        launchUI( {
            val newQuantity = ObjectQuantityAndNote(order.quantity,order.note)
            parserResponse(repository.updateOrderItem(newQuantity, order.objectId))
        },{
            defUI.showDialog.value =  (it as ResponseThrowable).errMsg
        })
    }

    /**
     * 单个验货
     */
    fun setCheckQuantity(newQuantity: ObjectCheckGoods, orders: OrderItem) {
        launchUI( {
            parserResponse(repository.setCheckQuantity(newQuantity, orders.objectId)) {
                ordersList.remove(orders)
                defUI.refreshEvent.call()
            }
        },{
            defUI.showDialog.value =  (it as ResponseThrowable).errMsg
        })
    }


    /**
     * 提交记帐
     */
    fun commitRecordState(orders: BatchOrdersRequest<ObjectState>): Completable {
        return repository.commitRecordState(orders)
    }

}