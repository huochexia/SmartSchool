package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCookBook
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewDailyMeal
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.None
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.Success
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.Goods
import kotlinx.coroutines.launch

/**
 * 使用协程
 */
class CookBookViewModel(
    private val respository: CookBookRepository
) : BaseViewModel() {


    val searchedStatusLiveData = MutableLiveData<SearchedStatus>(None)

    /*
    增加菜谱、菜单
     */
    fun createCookBook(newCookBook: NewCookBook) {
        viewModelScope.launch {
            respository.createCookBook(newCookBook)
        }
    }

    fun createDailyMeal(newDailyMeal: NewDailyMeal) {
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
    fun updateCookBook(newCookBook: NewCookBook, objectId: String) {
        viewModelScope.launch {
            respository.updateCookBook(newCookBook, objectId)
        }
    }

    fun updateDailyMeal(newDailyMeal: NewDailyMeal, objectId: String) {
        viewModelScope.launch {
            respository.updateDailyMeal(newDailyMeal, objectId)
        }
    }

    /*
    查询
     */
    fun getCookBookOfCategory(where: String) {
        val cookBook = respository.getCookBookOfCategory(where)
    }

    suspend fun getDailyMealOfDate(where: String) {
        val daily = respository.getDailyMealOfDate(where).await()
    }
    /*
    查找原材料,启动一个协程，在这个协程里调用挂起函数对数据库进行访问，并返回结果
     */
    fun searchMaterial(name: String) {
        viewModelScope.launch {
            val goods = respository.searchMaterial(name)
            if (goods.isEmpty()) {
                searchedStatusLiveData.value = None
            } else {
                searchedStatusLiveData.value = Success(goods)
            }
        }
    }
}