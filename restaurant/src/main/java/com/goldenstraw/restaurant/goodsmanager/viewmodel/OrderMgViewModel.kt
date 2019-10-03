package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.goldenstraw.restaurant.goodsmanager.adapter.CategoryAdapter
import com.goldenstraw.restaurant.goodsmanager.adapter.GoodsAdapter
import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.dao.CategoryAndAllGoods
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.util.toast
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * 订单管理的ViewModel
 */
class OrderMgViewModel(
    private val repository: GoodsRepository
) : BaseViewModel() {
    var categoryAndAllGoodsList = hashMapOf<String, List<Goods>>()

    //因为在这里得到数据，所有将列表适配器的创建也定义在ViewModel中
    lateinit var categoryAdapter: CategoryAdapter
    lateinit var goodsAdapter: GoodsAdapter

    @get:Bindable
    var categoryState = MultiStateView.VIEW_STATE_EMPTY
        set(value) {
            field = value
            notifyPropertyChanged(BR.categoryState)
        }
    @get:Bindable
    var goodsState = MultiStateView.VIEW_STATE_ERROR
        set(value) {
            field = value
            notifyPropertyChanged(BR.goodsState)
        }

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
                var categoryAllGoods: HashMap<String, List<Goods>> = hashMapOf()
                for (item in it) {
                    categoryAllGoods[item.category.categoryName] = item.goods
                }
                categoryAllGoods
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe(
                {
                    if (it.isNullOrEmpty()) {
                        categoryState = MultiStateView.VIEW_STATE_EMPTY
                        goodsState = MultiStateView.VIEW_STATE_EMPTY

                    } else {
                        categoryState = MultiStateView.VIEW_STATE_CONTENT
                        goodsState = MultiStateView.VIEW_STATE_CONTENT
                        categoryAndAllGoodsList = it //得到全部数据
                        val categoryList = getCategory() //提取类别
                        categoryAdapter = CategoryAdapter(getCategory())
                        //将类别列表的第一项做为选择的默认类别，显示它的所有商品
                        var goodsList = getGoodsList(categoryList[0])

                        goodsAdapter = GoodsAdapter(goodsList)
                    }
                }, {
                    categoryState = MultiStateView.VIEW_STATE_ERROR
                    goodsState = MultiStateView.VIEW_STATE_ERROR
                }, {
                },
                {
                    categoryState = MultiStateView.VIEW_STATE_LOADING
                    goodsState = MultiStateView.VIEW_STATE_LOADING
                })
    }

    /*
     *从CategoryAndAllGoods表中得到所有Category
     */
    fun getCategory(): List<String> {
        var categoryList = mutableListOf<String>()
        categoryAndAllGoodsList.keys.forEach {
            categoryList.add(it)
        }
        return categoryList
    }

    /*
     * 从CategroyAndAllGoods列表中，根据category得到其下所有商品列表
     */
    fun getGoodsList(categroy: String)
            : MutableList<Goods> {
        var goodsList = mutableListOf<Goods>()
        goodsList.addAll(categoryAndAllGoodsList[categroy]!!)
        return goodsList
    }
}