package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goldenstraw.restaurant.goodsmanager.adapter.CategoryAdapter
import com.goldenstraw.restaurant.goodsmanager.adapter.GoodsAdapter
import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.network.ApiException
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * 订单管理的ViewModel
 */
class OrderMgViewModel(
    private val repository: GoodsRepository
) : BaseViewModel() {

//    var categoryAndAllGoodsList = hashMapOf<GoodsCategory, List<Goods>>()


    //因为在这里得到数据，所有将列表适配器的创建也定义在ViewModel中
    var categoryList = mutableListOf<GoodsCategory>()
    var goodsList = mutableListOf<Goods>()
    val categoryState = ObservableField<Int>()
    val goodsState = ObservableField<Int>()
    //可观察数据
    private val isRefresh = MutableLiveData<Boolean>() //刷新列表
    private val state = MutableLiveData<Boolean>()  //弹出对话框

    /**
     * 初始化工作，获取数据，创建列表适配器
     */
    init {
        //因为这个ViewModel主要是对商品信息进行操作，所以初始化时需要直接获取所有商品信息
        getCategoryAndAllGoods()
    }

    /*
     *从本地数据库中获取所有类别,然后默认显示类别列表第一项的所有商品。
     */
    private fun getCategoryAndAllGoods() {
        repository.getAllCategory()
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
                        //将类别列表的第一项做为选择的默认类别，显示它的所有商品
                        repository.queryGoods(categoryList[0])
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .autoDisposable(this)
                            .subscribe({ goodslist ->
                                if (goodslist.isNullOrEmpty())
                                    goodsState.set(MultiStateView.VIEW_STATE_EMPTY)
                                else {
                                    goodsState.set(MultiStateView.VIEW_STATE_CONTENT)
                                    getGoodsList(goodsList)
                                }
                            }, {
                                goodsState.set(MultiStateView.VIEW_STATE_ERROR)
                            }, {}, {
                                goodsState.set(MultiStateView.VIEW_STATE_LOADING)
                            })
                    }
                }, {
                    categoryState.set(MultiStateView.VIEW_STATE_ERROR)
                }, {},
                {
                    categoryState.set(MultiStateView.VIEW_STATE_LOADING)
                })
    }

    /*
     *从CategoryAndAllGoods表中得到所有Category
     */
    private fun getCategory(list: MutableList<GoodsCategory>) {
        categoryList.clear()
        categoryList.addAll(list)
    }

    /*
     * 从CategroyAndAllGoods列表中，根据category得到其下所有商品列表
     */
    private fun getGoodsList(list: MutableList<Goods>) {
        goodsList.clear()
        goodsList.addAll(list)

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
        val newCategory = GoodsCategory(categoryName = category)
        repository.addGoodsCategory(newCategory)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe({
                categoryList.add(it)
                setRefresh(true)
            }, {

            })
    }
}