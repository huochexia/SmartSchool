package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.parserResponse
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QueryOrdersViewModel(
    private val repository: QueryOrdersRepository
) : BaseViewModel() {
    //所有供应商列表
    var suppliers = mutableListOf<User>() //供应商列表

    //用于查询某个供应商订单
    var supplier: String = ""

    //商品列表
    var goodsList = mutableListOf<Goods>()

    //订单列表
    var ordersList = mutableListOf<OrderItem>()

    //按商品类别进行分类的映射表
    var groupbyCategoryOfGoods = hashMapOf<String, MutableList<Goods>>()

    var viewState = ObservableField<Int>()


    /**
     * 获取所有供应商
     */
    fun getAllSupplier() {
        launchUI {
            viewState.set(MultiStateView.VIEW_STATE_LOADING)
            parserResponse(repository.getAllSupplier()) {
                if (it.isEmpty()) {
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                    suppliers.clear()
                } else {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                    suppliers = it
                }
            }
        }
    }

    /**
     * 按日期获取供应商订单
     */

    fun getAllOfOrders(where: String) {
        launchUI {
            viewState.set(MultiStateView.VIEW_STATE_LOADING)
            parserResponse(repository.getAllOfOrders(where)) {
                if (it.isEmpty()) {
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                    ordersList.clear()
                } else {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                    ordersList = it
                }
                defUI.refreshEvent.call()
            }
        }

    }

    /**
     *  发送到供应商
     */
    fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String) {
        launchUI {
            parserResponse(repository.updateOrderOfSupplier(newOrder, objectId)) {
                ordersList.removeIf {
                    it.objectId == objectId
                }
                defUI.refreshEvent.call()
            }
        }
        return
    }

    /**
     * 根据条件获取全部商品信息
     */
    fun getAllGoodsOfCategory(where: String) {
        launchUI {
            parserResponse(repository.getGoodsOfCategory(where)) {
                if (it.isEmpty()) {
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                    goodsList.clear()
                    goodsList.addAll(it)
                    defUI.refreshEvent.call()
                }
            }
        }

    }

    /**
     * 过滤类别，获取商品信息,主要是针对调料，粮油，豆乳品等类别价格变化不大供货商了解全部商品信息
     */
    fun getGoodsOfCategory(categoryId: String) {
        launchUI {
            val where = "{\"categoryCode\":\"$categoryId\"}"
            parserResponse(repository.getGoodsOfCategory(where)) {
                if (it.isEmpty()) {
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                    goodsList = it
                    goodsList.sortBy { goods ->
                        goods.objectId
                    }
                    defUI.refreshEvent.call()
                }

            }
        }
    }


    /**
     * 求和，计算当天购货总额
     */
    suspend fun getTotalOfSupplier(condition: String):ObjectList<SumResult> {
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
    fun updateNewPriceOfGoods(newPrice: NewPrice, objectId: String) {
        launchUI {
            parserResponse(repository.updateNewPrice(newPrice, objectId)) {
                defUI.refreshEvent.call()
            }
        }
    }


    /**
     * 删除订单
     */
    fun deleteOrderItem(objectId: String) {
        launchUI {
            parserResponse(repository.deleteOrderItem(objectId)) {
                defUI.refreshEvent.call()
            }
        }
    }

    /**
     * 修改订单数量和备注
     */
    fun updateOrderItem(newOrderItem: ObjectQuantityAndNote, objectId: String) {
        launchUI {
            parserResponse(repository.updateOrderItem(newOrderItem, objectId)) {
                defUI.refreshEvent.call()
            }
        }
    }

    /**
     * 查询到符合条件的订单，然后修改它的单价
     */
    fun updateUnitPriceOfOrders(where: String, price: Float) {
        launchUI {
            withContext(Dispatchers.IO) {
                parserResponse(repository.getOrdersList(where)) {
                    if (it.isNotEmpty()) {//不是空列表，有内容才能执行修改
                        val newPrice = ObjectUnitPrice(price)
                        repository.updateUnitPriceOfOrders(newPrice, it.first().objectId)
                    }
                }
            }

        }
    }
}