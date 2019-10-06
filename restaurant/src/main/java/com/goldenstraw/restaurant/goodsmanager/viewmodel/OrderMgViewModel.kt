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

    var categoryAndAllGoodsList = hashMapOf<GoodsCategory, List<Goods>>()
    private val state = MutableLiveData<Boolean>()

    //因为在这里得到数据，所有将列表适配器的创建也定义在ViewModel中
    var categoryList = mutableListOf<GoodsCategory>()

    var goodsList = mutableListOf<Goods>()
    val categoryState = ObservableField<Int>()
    val goodsState = ObservableField<Int>()

    /**
     * 初始化工作，获取数据，创建列表适配器
     */
    init {
        //因为这个ViewModel主要是对商品信息进行操作，所以初始化时需要直接获取所有商品信息
        getCategoryAndAllGoods()
    }

    /*
     *从本地数据库中获取所有类别及其商品,将数据转变成HashMap对象，key是类别名称，value是商品列表
     */

    fun getCategoryAndAllGoods() {
        repository.getCategory()
            .map {
                var categoryAllGoods: HashMap<GoodsCategory, List<Goods>> = hashMapOf()
                for (item in it) {
                    categoryAllGoods[item.category] = item.goods
                }
                categoryAllGoods
            }
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
                        goodsState.set(MultiStateView.VIEW_STATE_CONTENT)
                        categoryAndAllGoodsList = it //得到全部数据
                        getCategory()
                        //将类别列表的第一项做为选择的默认类别，显示它的所有商品
                        goodsList = getGoodsList(categoryList[0])
                        if (goodsList.isEmpty())
                            goodsState.set(MultiStateView.VIEW_STATE_EMPTY)
                    }
                    //无论是否有数据，都应该创建适配器，为了以后的增加
                }, {
                    categoryState.set(MultiStateView.VIEW_STATE_ERROR)
                    goodsState.set(MultiStateView.VIEW_STATE_ERROR)
                }, {

                },
                {
                    categoryState.set(MultiStateView.VIEW_STATE_LOADING)
                    goodsState.set(MultiStateView.VIEW_STATE_LOADING)
                })
    }

    /*
     *从CategoryAndAllGoods表中得到所有Category
     */
    private fun getCategory(): MutableList<GoodsCategory> {
        categoryAndAllGoodsList.keys.forEach {
            categoryList.add(it)
        }
        return categoryList
    }

    /*
     * 从CategroyAndAllGoods列表中，根据category得到其下所有商品列表
     */
    fun getGoodsList(categroy: GoodsCategory)
            : MutableList<Goods> {
        goodsList.addAll(categoryAndAllGoodsList[categroy]!!)
        return goodsList
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
            }, {

            })
    }
}