package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import com.goldenstraw.restaurant.goodsmanager.http.entities.CookBook
import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.None
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.Success
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.Goods
import kotlinx.coroutines.launch

/**
 * 使用协程。
 */
class CookBookViewModel(
    private val respository: CookBookRepository
) : BaseViewModel() {


    val searchedStatusLiveData = MutableLiveData<SearchedStatus>(None)
    val createSuccess = MutableLiveData<String>()
    /*
       刷新菜谱原材料列表，当在查询商品的列表中选择某一个原材料后，将该材料加入原材料列表中，
       然后刷新列表显示。
       通过这种共享方式实现SearchMaterialFragment中的选择，显示在InputCookBookFragment中。
     */

    val materialList = mutableListOf<Goods>()
    var cookbookList = mutableListOf<CookBook>()
    /*
    增加菜谱、菜单
     */
    fun createCookBook(newCookBook: CookBook) {
        launchUI {
            newCookBook.save(object : SaveListener<String>() {
                override fun done(id: String?, e: BmobException?) {
                    if (e == null) {
                        cookbookList.add(newCookBook)
                        materialList.clear()//保存成功，清除原材料列表
                        defUI.refreshEvent.call()
                    } else {
                        //发出显示错误信息
                        defUI.showDialog.postValue(e.message)
                    }
                }
            })
        }
    }
    /*
     获取某类菜谱列表
     */

    fun createDailyMeal(newDailyMeal: DailyMeal) {
        viewModelScope.launch {
            respository.createDailyMeal(newDailyMeal)
        }
    }

    /*
    删除
     */
    fun deleteCookBook(objectId: String) {
        viewModelScope.launch {
            respository.deleteCookBook(objectId)
        }
    }

    fun deleteDailyMeal(objectId: String) {
        viewModelScope.launch {
            respository.deleteDailyMeal(objectId)
        }
    }

    /*
    更新
     */
    fun updateCookBook(newCookBook: CookBook, objectId: String) {
        viewModelScope.launch {
            respository.updateCookBook(newCookBook, objectId)
        }
    }

    fun updateDailyMeal(newDailyMeal: DailyMeal, objectId: String) {
        viewModelScope.launch {
            respository.updateDailyMeal(newDailyMeal, objectId)
        }
    }

    /*
    查询
     */
    fun getCookBookOfCategory(category: String) {
        launchUI {
            val query = BmobQuery<CookBook>()
            query.addWhereEqualTo("foodCategory", category)
            query.findObjects(object : FindListener<CookBook>() {
                override fun done(list: MutableList<CookBook>?, e: BmobException?) {
                    if (e == null) {
                        list?.let {
                            cookbookList = list
                        }
                        defUI.refreshEvent.call()
                    } else {
                        defUI.showDialog.postValue(e.message)
                    }
                }

            })
        }
    }

    suspend fun getDailyMealOfDate(where: String) {
        val daily = respository.getDailyMealOfDate(where).await()
    }
    /*
    从本地数据中查找原材料（主要因为Bomb模糊查询要收费）
    启动一个协程，在这个协程里调用挂起函数对数据库进行访问，并返回结果
     */
    fun searchMaterial(name: String) {
        launchUI {
            val goods = respository.searchMaterial(name)
            if (goods.isEmpty()) {
                searchedStatusLiveData.value = None
            } else {
                searchedStatusLiveData.value = Success(goods)
            }
        }
    }
}