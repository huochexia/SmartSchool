package com.goldenstraw.restaurant.goodsmanager.viewmodel

import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.dao.CategoryAndAllGoods
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.uber.autodispose.autoDisposable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * 订单管理的ViewModel
 */
class OrderMgViewModel(
    private val repository: GoodsRepository
) : BaseViewModel() {
    lateinit var categoryList: MutableList<CategoryAndAllGoods>
    lateinit var goodsList: MutableList<Goods>
    /*
     *从本地数据库中获取所有类别及其商品
     */
    fun getCategory(): MutableList<CategoryAndAllGoods> {
        repository.getCategory()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this)
            .subscribe {
                categoryList = it
            }
        return categoryList
    }
}