package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
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


/**
 * 订单管理的ViewModel
 */
class GoodsToOrderMgViewModel(
     val repository: GoodsRepository
) : BaseViewModel() {


    //因为在这里得到数据，所有将列表适配器的创建也定义在ViewModel中
    var categoryList = mutableListOf<GoodsCategory>()

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

    //分页
    var allGoods: LiveData<PagedList<Goods>>? = null
    /**
     * 初始化工作，获取数据，创建列表适配器
     */
    init {
        //因为这个ViewModel主要是对商品信息进行操作，所以初始化时需要直接获取所有商品信息
        getAllCategory()
//        getCountOfShoppingCart()

    }


    /*
     *从本地数据库中获取所有类别,然后默认显示类别列表第一项的所有商品。
     */
    private fun getAllCategory() {
        repository.getAllCategoryFromLocal()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe(
                {
                    if (it.isNullOrEmpty()) {
                        categoryState.set(MultiStateView.VIEW_STATE_EMPTY)
                        goodsState.set(MultiStateView.VIEW_STATE_EMPTY)
                    } else {
                        categoryState.set(MultiStateView.VIEW_STATE_CONTENT)
                        getCategory(it)
                        it.first().isSelected = true
                        selected.value = it.first()
                    }
                }, {
                    categoryState.set(MultiStateView.VIEW_STATE_ERROR)
                }, {

                },
                {
                    categoryState.set(MultiStateView.VIEW_STATE_LOADING)
                })
    }

    /*
     *从本地得到所有Category
     */
    private fun getCategory(list: MutableList<GoodsCategory>) {
        categoryList.clear()
        categoryList.addAll(list)

    }

    /*
     * 从CategroyAndAllGoods列表中，根据category得到其下所有商品列表
     */
    fun getGoodsOfCategory(category: GoodsCategory) {
        //将类别列表的第一项做为选择的默认类别，显示它的所有商品
        repository.queryGoodsFromLocal(category)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({ list ->
                if (list.isNullOrEmpty()) {
                    goodsState.set(MultiStateView.VIEW_STATE_EMPTY)
                    goodsList.clear()//清空前面选择时得到的内容
                } else {
                    goodsState.set(MultiStateView.VIEW_STATE_CONTENT)
                    getGoodsList(list)
                }
            }, {
                goodsState.set(MultiStateView.VIEW_STATE_ERROR)
            }, {
                //在这里同步数据库
            }, {
                goodsState.set(MultiStateView.VIEW_STATE_LOADING)
            })
    }

    private fun getGoodsList(list: MutableList<Goods>) {
        goodsList.clear()
        goodsList.addAll(list)
    }

    /*
    使用分页获取商品列表
     */
    fun getPagingOfGoods(category: GoodsCategory) {
        allGoods = LivePagedListBuilder(
            repository.getAllGoodsOfPaging(category),
            PagedList.Config.Builder()
                .setPageSize(10)
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2) //距底部还有几条数据时，加载下一页
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
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
                categoryList.add(it)
                setRefresh(true)
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
                goodsList.add(it)
                isGoodsListRefresh.value = true
            }, {

            })
    }

    /**
     * 将所选择商品加入购物车
     */
    fun addGoodsToShoppingCart(list: MutableList<Goods>) {
        val selectedGoods = mutableListOf<GoodsOfShoppingCart>()
        list.forEach {
            if (it.isChecked) {
                var goods = GoodsOfShoppingCart(
                    code = it.objectId,
                    quantity = it.quantity.toFloat(),
                    categoryCode = it.categoryCode,
                    goodsName = it.goodsName,
                    unitPrice = it.unitPrice,
                    unitOfMeasurement = it.unitOfMeasurement
                )
                selectedGoods.add(goods)
            }
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
                            .subscribe{category->
                                repository.getAllGoodsOfCategoryFromNetwork(category)
                                    .autoDisposable(this)
                                    .subscribe{goodslist->
                                        repository.addGoodsListToLocal(goodslist)
                                            .autoDisposable(this)
                                            .subscribe({}, {})
                                    }
                            }
                    }

            }, {})

    }

    /**
     * 获取某日菜单，并将其保存在购物车当中
     */
    fun getDailyMealToShoppingCar(where: String) {
        repository.getDailyMealOfDate(where)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Observable.fromIterable(it.results)
            }
            .flatMap {
                Observable.fromIterable(it.cookBook.material)
            }
            .distinct()
            .autoDisposable(this)
            .subscribe({ goods ->
                goods.isChecked = true
                materialList.add(goods)
            }, {

            }, {
                addGoodsToShoppingCart(materialList)
                defUI.refreshEvent.call()
            }, {
                materialList.clear()
            })
    }
}