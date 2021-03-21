package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.network.ObjectList
import com.owner.basemodule.network.parserResponse
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.Material
import com.owner.basemodule.room.entities.User
import com.owner.basemodule.util.TimeConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QueryOrdersViewModel(
    private val repository: QueryOrdersRepository
) : BaseViewModel() {
    //所有供应商列表
    var suppliers = mutableListOf<User>() //供应商列表

    //用于查询某个供应商订单
    var supplier: String = ""

    //商品列表,主要用于让供应商调整价格。
    //它有可能列示的是全部商品，也有可能列示的是即将要提供的商品
    var goodsList = mutableListOf<Goods>()

    //订单列表
    var ordersList = mutableListOf<OrderItem>()

    //分组计算订单合计金额列表
    var details = mutableListOf<SumByGroup>()

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

    /*******************************************************************
     * 订单操作
     *******************************************************************/
    /*
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

    /*
     * 删除订单
     */
    fun deleteOrderItem(objectId: String) {
        launchUI {
            parserResponse(repository.deleteOrderItem(objectId)) {
                defUI.refreshEvent.call()
            }
        }
    }

    /*
     * 修改订单数量和备注
     */
    fun updateOrderItem(newOrderItem: ObjectQuantityAndNote, objectId: String) {
        launchUI {
            parserResponse(repository.updateOrderItem(newOrderItem, objectId)) {
                defUI.refreshEvent.call()
            }
        }
    }

    /*
     *  发送订单到供应商
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

    /**********************************************************************
     * 调整价格部分
     *********************************************************************/
    /*
     * 根据条件获取全部商品信息
     *
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

    /*
     * 按类别，获取某类全部商品信息,主要是针对调料，粮油。
     * 豆乳品等类别价格变化不大供货商了解全部商品信息
     */
    fun getGoodsOfCategory(categoryId: String) {
        launchUI {
            viewState.set(MultiStateView.VIEW_STATE_LOADING)

            val where = "{\"categoryCode\":\"$categoryId\"}"
            parserResponse(repository.getGoodsOfCategory(where)) {
                if (it.isEmpty()) {
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                    goodsList.clear()
                } else {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                    goodsList = it
                    goodsList.sortBy { goods ->
                        goods.objectId
                    }
                }
                defUI.refreshEvent.call()
            }
        }
    }

    /*
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

    /*
     * 提交新单价
     */
    fun updateNewPriceOfGoods(newPrice: NewPrice, objectId: String) {
        launchUI {
            parserResponse(repository.updateNewPrice(newPrice, objectId)) {
                defUI.refreshEvent.call()
            }
        }
    }

    /*
      获取某日及以后5天内可能需要的商品列表
      @categoryId ：类别号，如果类别号为空，则显示全部商品，否则显示指定类别的商品
     */
    fun getGoodsOfFutureNeed(categoryId: String = "") {

        viewState.set(MultiStateView.VIEW_STATE_LOADING)
        launchUI {
            //1.获取当天日期及其后5天日期的字符串列表
            val afterDateList = TimeConverter.getFromCurrentToAfter(5)
            val dailyMeals = mutableListOf<DailyMeal>()

            withContext(Dispatchers.IO) {
                //2.获取所有日期的菜单
                afterDateList.forEach {
                    val where = "{\"mealDate\":\"$it\"}"
                    dailyMeals.addAll(getDailyMealOfGreatOrEqual(where))
                }
                //3.从获取菜单中得到菜谱中的材料列表
                val materialsList = getAllOfMaterialFromDailyMeal(dailyMeals)

                if (materialsList.isNotEmpty()) {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                    goodsList = materialsList.distinct()
                        .filter {
                            if (categoryId.isNotEmpty()) {
                                it.categoryCode == categoryId
                            } else {
                                true
                            }
                        } as MutableList
                } else {
                    goodsList.clear()
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                }
                defUI.refreshEvent.call()
            }

        }
    }


    /*
    获取每日菜单列表
     */
    private suspend fun getDailyMealOfGreatOrEqual(where: String): MutableList<DailyMeal> {
        var list = mutableListOf<DailyMeal>()
        parserResponse(repository.getDailyMeals(where)) {
            list = it
        }
        return list
    }

    /*
      获取每日菜单当中的菜谱
     */
    private suspend fun getAllOfMaterialFromDailyMeal(dailyMeals: List<DailyMeal>): MutableList<Goods> {
        val goodsList = mutableListOf<Goods>()
        dailyMeals.forEach {
            val remoteCookBook = repository.getCookBook(it.cookBook.objectId)
            remoteCookBook.material.forEach { material ->
                goodsList.add(getMaterialToGoods(material))
            }
        }
        return goodsList
    }

    /*
      根据菜谱中的材料，获取对应的商品
     */
    private suspend fun getMaterialToGoods(material: Material): Goods {

        return repository.getGoods(material.goodsId)

    }


    /*******************************************************************
     * 汇总进行计算求和
     *******************************************************************/
/*
 * 求和，计算当天购货总额
 */
    suspend fun getTotalOfSupplier(condition: String): ObjectList<SumResult> {
        return repository.getTotalOfSupplier(condition)
    }

    /*
     * 分组求和
     */
    fun getTotalGroupByName(condition: String) {
        launchUI {
            viewState.set(MultiStateView.VIEW_STATE_LOADING)
            parserResponse(repository.getTotalGroupByName(condition)) {

                if (it.isEmpty()) {
                    details.clear()
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                    details = it
                }
                defUI.refreshEvent.call()
            }
        }

    }

}