package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.network.parserResponse
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QueryOrdersViewModel(
    private val repository: QueryOrdersRepository
) : BaseViewModel() {
    //所有供应商列表
    val suppliers = mutableListOf<User>() //供应商列表

    //用于查询某个供应商订单
    var supplier: String = ""

    //商品列表
    var goodsList = mutableListOf<Goods>()

    //按商品类别进行分类的映射表
    var groupbyCategoryOfGoods = hashMapOf<String, MutableList<Goods>>()

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

    fun getAllOfOrders(where: String): Observable<MutableList<OrderItem>> {
        return repository.getAllOfOrders(where)
    }

    /**
     *  发送到供应商
     */
    fun updateOrderOfSupplier(newOrder: ObjectSupplier, objectId: String): Completable {
        return repository.updateOrderOfSupplier(newOrder, objectId)
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