package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCategory
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.goldenstraw.restaurant.goodsmanager.repositories.goods_order.GoodsRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext


/**
 * 订单管理的ViewModel
 */
class GoodsToOrderMgViewModel(
    val repository: GoodsRepository
) : BaseViewModel() {


    //因为在这里得到数据，所有将列表适配器的创建也定义在ViewModel中
//    var categoryList = mutableListOf<GoodsCategory>()

    var goodsList = mutableListOf<Goods>() //商品列表
    var materialList = mutableListOf<Goods>()//菜谱中原材料列表

    var searchGoodsResultList = mutableListOf<Goods>() //根据商品名称搜索结果列表
    val categoryState = ObservableField<Int>()
    val goodsState = ObservableField<Int>()

    //可观察数据
    private val isRefresh = MutableLiveData<Boolean>() //刷新类别列表
    var isGoodsListRefresh = MutableLiveData<Boolean>()//刷新商品列表
    private val state = MutableLiveData<Boolean>()  //弹出对话框
    var selected = MutableLiveData<GoodsCategory>() //当前选择的商品类别
    var shoppingCartOfQuantity = MutableLiveData<Int>()


    /**
     * 使用Flow方式得到数据并转化为LiveData
     */
    val categoryListFlow = repository.categorysFlow
        .asLiveData()

    fun fetchGoodsListFlow(categoryId: String) = liveData<List<Goods>> {
        repository.getGoodsOfCategoryFromLocalFlow(categoryId)
            .onStart {
                goodsState.set(MultiStateView.VIEW_STATE_LOADING)
            }
            .catch {
                goodsState.set(MultiStateView.VIEW_STATE_ERROR)
            }
            .collectLatest {
                if (it.isNullOrEmpty()) {
                    goodsState.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    goodsState.set(MultiStateView.VIEW_STATE_CONTENT)
                    goodsList = it as MutableList<Goods>//暂时的，用于做类别删除时判断是否有内容
                    emit(it)
                }
            }
    }

    /*
    根据商品名称进行模糊查询
     */
    fun searchGoodsFromName(name: String) {
        repository.findByName(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({
                searchGoodsResultList.clear()
                searchGoodsResultList.addAll(it)
                isRefresh.value = true
            }, {}, {})
    }

    private fun getSearchResultList(results: MutableList<Goods>) {
        searchGoodsResultList.clear()
        searchGoodsResultList.addAll(results)
    }

    /**
     * 删除商品信息或类别
     */
    fun deleteGoods(goods: Goods): Completable {
        return repository.deleteGoodsFromLocal(goods)
            .andThen(repository.deleteGoodsFromRemote(goods))
    }

    fun deleteCategory(category: GoodsCategory): Completable {
        return repository.deleteCategoryFromLocal(category)
            .andThen(repository.deleteCategoryFromRemote(category))
    }

    /**
     * 修改信息
     */
    fun updateGoods(goods: Goods): Completable {
        return repository.updateGoods(goods)
            .andThen(repository.insertGoodsToLocal(goods))
    }

    fun updateCategory(category: GoodsCategory): Completable {
        return repository.updateCategory(category)
            .andThen(repository.insertCategoryToLocal(category))
    }

    /*
       状态管理
     */
    fun getState(): LiveData<Boolean> {
        return state
    }

    fun setAddCategoryDialogState(isShownDialog: Boolean) {
        state.value = isShownDialog
    }

    /*
       通知列表刷新
     */
    fun getIsRefresh(): LiveData<Boolean> {
        return isRefresh
    }

    fun setRefresh(isRefresh: Boolean) {
        this.isRefresh.value = isRefresh
    }

    /*
      保存新增类别到数据库中
     */
    fun addCategoryToRepository(category: String) {
        val newCategory = NewCategory(category)
        repository.addGoodsCategory(newCategory)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({
                repository.insertCategoryToLocal(it).subscribeOn(Schedulers.newThread())
                    .autoDisposable(this)
                    .subscribe()
            }, {

            })
    }

    /*
    保存新增加商品到数据库中
     */
    fun addGoodsToRepository(goods: NewGoods) {
        repository.addGoods(goods)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({
                repository.insertGoodsToLocal(it).subscribeOn(Schedulers.newThread())
                    .autoDisposable(this)
                    .subscribe()
                goodsState.set(MultiStateView.VIEW_STATE_CONTENT)
//                goodsList.add(it)
//                isGoodsListRefresh.value = true
            }, {

            })
    }

    /**
     * 将所选择商品加入购物车
     */
    fun addGoodsToShoppingCart(foodCategory: String, list: MutableList<Goods>) {
        val selectedGoods = mutableListOf<GoodsOfShoppingCart>()
        Observable.fromIterable(list)
            .distinct {
                it.goodsName
            }
            .autoDisposable(this)
            .subscribe{
                if (it.isChecked) {
                    val goods = GoodsOfShoppingCart(
                        objectId = it.objectId,
                        quantity = it.quantity.toFloat(),
                        categoryCode = it.categoryCode,
                        goodsName = it.goodsName,
                        unitPrice = it.unitPrice,
                        unitOfMeasurement = it.unitOfMeasurement,
                        foodCategory = foodCategory
                    )
                    selectedGoods.add(goods)
            }
//        list.forEach {
//
//            }
        }
        repository.addGoodsToShoppingCart(selectedGoods)
            .subscribeOn(Schedulers.io())
            .autoDisposable(this)
            .subscribe({
                //添加成功后获取购物车中的商品数量
                getCountOfShoppingCart()
            }, {

            })
    }

    /**
     * 获取购物车中商品数量
     */
    fun getCountOfShoppingCart() {
        repository.getShoppingCartOfCount()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({
                shoppingCartOfQuantity.value = it
            }, {})
    }

    /**
     * 同步类别和商品信息
     */
    fun syncAllData() {
        repository.clearAllData()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({
                repository.getAllCategoryFromNetwork()
                    .subscribeOn(Schedulers.io())
                    .autoDisposable(this)
                    .subscribe {
                        repository.addCategoryListToLocal(it)
                            .subscribeOn(Schedulers.newThread())
                            .autoDisposable(this)
                            .subscribe({
                            }, {

                            })
                        Observable.fromIterable(it)
                            .subscribeOn(Schedulers.newThread())
                            .autoDisposable(this)
                            .subscribe { category ->
                                repository.getAllGoodsOfCategoryFromNetwork(category)
                                    .autoDisposable(this)
                                    .subscribe { goodslist ->
                                        repository.addGoodsListToLocal(goodslist)
                                            .autoDisposable(this)
                                            .subscribe({}, {})
                                    }
                            }
                    }

            }, {})

    }

    /**
     * 获取某日菜单将其所需商品，汇总后保存在购物车当中
     */
    val groupbyFoodCategory = hashMapOf<String, MutableList<Goods>>()

    fun getDailyMealToShoppingCar(where: String) {
//        materialList.clear()
        groupbyFoodCategory.clear()
        launchUI {
            withContext(Dispatchers.IO) {
                val dailyMeals = repository.getDailyMealOfDate(where)
                if (dailyMeals.isSuccess()) {
                    Observable.fromIterable(dailyMeals.results)
                        .groupBy {
                            it.cookBook.foodCategory
                        }
                        .autoDisposable(this@GoodsToOrderMgViewModel)
                        .subscribe({ group ->
                            groupbyFoodCategory[group.key!!] = mutableListOf()
                            group.autoDisposable(this@GoodsToOrderMgViewModel)
                                .subscribe {
                                    val cookbook =
                                        repository.getCookBookWithGoods(it.cookBook.objectId)
                                    //这里使用空安全是因为DailyMeal与CookBook没有进行关联，可能存在CookBook已经删除
                                    //但历史的DailyMeal还包含它。如果复制使用这个DailyMeal，这样就会存在空指针。
                                    cookbook?.let { cookBookWithGoods ->
                                        groupbyFoodCategory[group.key]!!.addAll(cookBookWithGoods.goods)
                                    }
                                }

                        }, {
                            throw it
                        }, {
                            groupbyFoodCategory.values.forEach { goodsList ->
                                goodsList.forEach { goods ->
                                    goods.isChecked = true

                                }
                            }
                            groupbyFoodCategory.keys.forEach { foodCategory ->
                                addGoodsToShoppingCart(
                                    foodCategory,
                                    groupbyFoodCategory[foodCategory]!!
                                )
                            }
                        })
                }
            }
            defUI.refreshEvent.call()
        }
//        launchUI {
//            //第一步在IO线程上获取菜单
//            withContext(Dispatchers.IO) {
//                val dailyMealList = repository.getDailyMealOfDate(where)
//                if (dailyMealList.isSuccess()) {
//                    //第二步，在Dispatchers.Default中获取商品。因为Room有自已的Dispatcher，不对Room使用IO
//                    withContext(Dispatchers.Default) {
//                        dailyMealList.results!!.forEach {
//                            val cookbook = repository.getCookBookWithGoods(it.cookBook.objectId)
//                            cookbook?.let {
//                                materialList.addAll(cookbook.goods)
//                            }
//                        }
//                    }
//                    materialList.forEach {
//                        it.isChecked = true
//                    }
//
//                } else {
//                    defUI.showDialog.value = dailyMealList.error
//                }
//            }
//            addGoodsToShoppingCart(materialList)
//            defUI.refreshEvent.call()
//        }

    }
}