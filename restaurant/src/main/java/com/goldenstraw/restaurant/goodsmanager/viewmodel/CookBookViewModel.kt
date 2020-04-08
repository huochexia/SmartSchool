package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.lifecycle.viewModelScope
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCookBook
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewDailyMeal
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.owner.basemodule.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class CookBookViewModel(
    private val respository: CookBookRepository
) : BaseViewModel() {

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
}