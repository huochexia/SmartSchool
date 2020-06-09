package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.User
import com.owner.basemodule.util.TimeConverter
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class QueryOrdersViewModel(
    private val repository: QueryOrdersRepository
) : BaseViewModel() {
    //所有供应商列表
    val suppliers = mutableListOf<User>() //供应商列表

    //用于查询某个供应商订单
    var supplier: String = ""

    //商品列表
    var goodsList = mutableListOf<Goods>()

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
    fun getAllGoodsOfCategory(where: String): Observable<MutableList<Goods>> {
        return repository.getGoodsOfCategory(where)
    }

    /**
     * 过滤类别，获取商品信息,主要是针对调料，粮油，豆乳品等类别价格变化不大供货商了解全部商品信息
     */
    fun getGoodsOfCategory(categoryId: String) {

        val where = "{\"categoryCode\":\"$categoryId\"}"
        repository.getGoodsOfCategory(where).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({ list ->
                goodsList = list
                goodsList.sortBy {
                    it.objectId
                }
            }, {
                defUI.toastEvent.value = it.message
            }, {
                defUI.refreshEvent.call()
            }, {
                defUI.loadingEvent.call()
            })
    }

    /**
     * 使用类别过滤，部分类别供货商需要了解下周学校可能使用的商品，以便对价格及时调整。
     * 通过获取每日菜单当中的菜谱信息,最后得到商品信息
     *
     */
    fun getCookBookOfDailyMeal(categoryId: String) {

        Observable.fromIterable(TimeConverter.getNextWeekToString(Date(System.currentTimeMillis())))
            .flatMap { date ->
                val where = "{\"mealDate\":\"$date\"}"
                repository.getCookBookOfDailyMeal(where)//从远程得到某日期菜单结果列表
            }.flatMap {
                Observable.fromIterable(it.results)//从远程数据结果当中得到菜单列表
            }
            .map {
                it.cookBook.material //从菜谱中得到商品列表
            }
            .flatMap {
                Observable.fromIterable(it)
            }
            .filter {
                it.categoryCode == categoryId  //过滤类别

            }
            .distinct() //去重复
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({ goods ->
                goodsList.add(goods)
            }, {
                defUI.toastEvent.value = it.message
            }, {
                defUI.refreshEvent.call()
            }, {
                goodsList.clear()
                defUI.loadingEvent.call()
            })
    }

    /**
     *
     *  不过滤,用于管理员查看下周可能需要的所有商品
     */
    fun getAllCookBookOfDailyMeal() {
        goodsList.clear()
        Observable.fromIterable(TimeConverter.getNextWeekToString(Date(System.currentTimeMillis())))
            .flatMap { date ->
                val where = "{\"mealDate\":\"$date\"}"
                repository.getCookBookOfDailyMeal(where)//从远程得到某日期菜单结果列表
            }.flatMap {
                Observable.fromIterable(it.results)//从远程数据结果当中得到菜单列表
            }
            .map {
                it.cookBook.material //从菜谱中得到商品列表
            }.flatMap {
                Observable.fromIterable(it)
            }
            .distinct() //去重复
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({ goods ->
                goodsList.add(goods)
            }, {
                defUI.toastEvent.value = it.message
            }, {
                defUI.refreshEvent.call()
                goodsList.sortBy {
                    it.categoryCode
                }
            }, {
                defUI.loadingEvent.call()
            })
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
    fun updateNewPriceOfGoods(newPrice: NewPrice, objectId: String): Completable {
        return repository.updateNewPrice(newPrice, objectId)

    }

    /**
     * 删除订单
     */
    fun deleteOrderItem(objectId: String) {
        launchUI {
            repository.deleteOrderItem(objectId)
            defUI.refreshEvent.call()
        }
    }

    /**
     * 修改订单数量和备注
     */
    fun updateOrderItem(newOrderItem: ObjectQuantityAndNote, objectId: String) {
        launchUI {
            repository.updateOrderItem(newOrderItem, objectId)
            defUI.refreshEvent.call()
        }
    }
}