package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import com.goldenstraw.restaurant.goodsmanager.http.entities.CookBook
import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewDailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.UpdateIsteacher
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.None
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.Success
import com.goldenstraw.restaurant.goodsmanager.utils.CookKind
import com.goldenstraw.restaurant.goodsmanager.utils.MealTime
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.Goods
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 使用协程。对菜谱的管理（增删），对每日菜单的管理（增改）
 */
class CookBookViewModel(
    private val repository: CookBookRepository
) : BaseViewModel() {

    /**
     * 对菜谱的管理部分
     */
    val searchedStatusLiveData = MutableLiveData<SearchedStatus>(None)

    /*
      刷新菜谱原材料列表，当在查询商品的列表中选择某一个原材料后，将该材料加入原材料列表中，
      然后刷新列表显示。
      通过这种共享方式实现SearchMaterialFragment中的选择，显示在InputCookBookFragment中。
    */

    val materialList = mutableListOf<Goods>()
    var cookbookList = mutableListOf<CookBook>()

    //分组列表，key-value:key为小类，value为内容列表
    var groupbyKind = hashMapOf<String, MutableList<CookBook>>()



    /*
     一、 增加菜谱
     */
    fun createCookBook(newCookBook: CookBook) {
        launchUI {
            newCookBook.save(object : SaveListener<String>() {
                override fun done(id: String?, e: BmobException?) {
                    if (e == null) {
//                        cookbookList.add(newCookBook)
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
    二、删除
     */
    fun deleteCookBook(objectId: String) {
        viewModelScope.launch {
            repository.deleteCookBook(objectId)
        }
    }

    /*
    查询，使用协程调用Bmob的Api进行查询。对结果通过groupBy进行分组。
     */
    suspend fun getCookBookOfCategory2(category: String) {
        launchFlow {
            val where = "\"foodCategory\":\"$category\""
            repository.getCookBookOfCategory(where)
        }.onStart {
            groupbyKind.clear()
        }.collect {
            if (it.isSuccess()) {
                cookbookList = it.results as MutableList<CookBook>
                Observable.fromIterable(cookbookList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .groupBy {
                        it.foodKind
                    }
                    .autoDisposable(this@CookBookViewModel)
                    .subscribe({
                        groupbyKind[it.key!!] = mutableListOf()//为每个分类建立key-value值
                        it.autoDisposable(this@CookBookViewModel)
                            .subscribe { cookbook ->
                                groupbyKind[it!!.key]!!.add(cookbook)//将对应分类的菜谱，存入对应的列表中
                            }
                    }, {}, {
                        defUI.refreshEvent.call()//发出刷新数据通知
                    })
            } else {
                defUI.showDialog.postValue(it.error)
            }
        }

    }

    fun getCookBookOfCategory(category: String) {
        //清空原内容
        groupbyKind.clear()
        launchUI {

            val query = BmobQuery<CookBook>()
            query.addWhereEqualTo("foodCategory", category)
            query.setLimit(500)
            query.findObjects(object : FindListener<CookBook>() {
                override fun done(list: MutableList<CookBook>?, e: BmobException?) {
                    if (e == null) {
                        //将列表按小类分组，使用Rxjava的groupBy按小类进行分组

                        list?.let { list ->
                            cookbookList = list
                            Observable.fromIterable(cookbookList)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .groupBy {
                                    it.foodKind
                                }
                                .autoDisposable(this@CookBookViewModel)
                                .subscribe({
                                    groupbyKind[it.key!!] = mutableListOf()//为每个分类建立key-value值
                                    it.autoDisposable(this@CookBookViewModel)
                                        .subscribe { cookbook ->
                                            groupbyKind[it!!.key]!!.add(cookbook)//将对应分类的菜谱，存入对应的列表中
                                        }
                                }, {}, {
                                    defUI.refreshEvent.call()//发出刷新数据通知
                                })
                        }

                    } else {
                        defUI.showDialog.postValue(e.message!!)
                    }
                }

            })
        }
    }


    /*
    从本地数据中查找原材料（主要因为Bomb模糊查询要收费）
    启动一个协程，在这个协程里调用挂起函数对数据库进行访问，并返回结果
     */
    fun searchMaterial(name: String) {
        launchUI {
            val goods = repository.searchMaterial(name)
            if (goods.isEmpty()) {
                searchedStatusLiveData.value = None
            } else {
                searchedStatusLiveData.value = Success(goods)
            }
        }
    }

    /**
     * 对每日菜单的管理部分
     */
    //五个类型菜品的列表
    var coldList = mutableListOf<DailyMeal>()
    var hotList = mutableListOf<DailyMeal>()
    var flourList = mutableListOf<DailyMeal>()
    var soupList = mutableListOf<DailyMeal>()
    var snackList = mutableListOf<DailyMeal>()

    //清空所有列表内容
    fun clearAllList() {
        coldList.clear()
        hotList.clear()
        flourList.clear()
        soupList.clear()
        snackList.clear()
    }

    //可观察对象，通过观察它的值来判断哪个列表需要刷新
    // 通过传入的日菜单，来通知视图现在操作的是那类列表
    private val _refreshAdapter = MutableLiveData<String>() //私有的
    val refreshAdapter: LiveData<String> =
        _refreshAdapter //对外暴露不可变的。因为LiveData的setValue方法是protected

    fun setRefreshAdapter(dailyMeal: DailyMeal) {
        _refreshAdapter.value = dailyMeal.cookBook.foodCategory
    }

    //获取某日菜单，将结果分组存入不同的列表
    fun getDailyMealOfDate(where: String) {
        launchUI {
            launchFlow {
                repository.getDailyMealOfDate(where)
            }.flowOn(Dispatchers.IO)
                .collect {
                    if (it.isSuccess()) {
                        clearAllList()//先清空列表，然后将新结果加入
                        Observable.fromIterable(it.results)
                            .groupBy { meal ->
                                meal.cookBook.foodCategory
                            }
                            .autoDisposable(this@CookBookViewModel)
                            .subscribe({ group ->
                                group.autoDisposable(this@CookBookViewModel)
                                    .subscribe { dailies ->
                                        when (group.key) {
                                            CookKind.ColdFood.kindName -> coldList.add(dailies)
                                            CookKind.HotFood.kindName -> hotList.add(dailies)
                                            CookKind.FlourFood.kindName -> flourList.add(dailies)
                                            CookKind.SoutPorri.kindName -> soupList.add(dailies)
                                            CookKind.Snackdetail.kindName -> snackList.add(dailies)
                                        }
                                    }
                            }, {}, {
                                _refreshAdapter.value = "All"
                            })
                    }
                }

        }
    }

    /*
    拷贝某一天菜单
     */
    fun copyDailyMeal(newDate:String,oldDate:String) {
        launchUI {
            launchFlow {
                val where="{\"mealDate\":\"$oldDate\"}"
                repository.getDailyMealOfDate(where)
            }.flowOn(Dispatchers.IO)
                .collect {
                    if (it.isSuccess()) {
                        Observable.fromIterable(it.results)
                            .map { oldDailyMeal ->
                                val newDailyMeal = NewDailyMeal(
                                    oldDailyMeal.mealTime,
                                    newDate,
                                    oldDailyMeal.cookBook,
                                    oldDailyMeal.isOfTeacher

                                )
                                newDailyMeal
                            }
                            .autoDisposable(this@CookBookViewModel)
                            .subscribe({ new ->
                                createDailyMeal(new)
                            }, {}, {
                                val where =
                                    "{\"\$and\":[{\"mealTime\":\"${MealTime.Breakfast.time}\"}" +
                                            ",{\"mealDate\":\"$newDate\"}]}"
                                getDailyMealOfDate(where)
                            })
                    }
                }
        }
    }

    fun updateDailyMeal(newDailyMeal: UpdateIsteacher, objectId: String) {
        launchUI {
            repository.updateDailyMeal(newDailyMeal, objectId)
        }
    }

    fun deleteDailyMeal(objectId: String) {
        launchUI {
            repository.deleteDailyMeal(objectId)
        }
    }

    /*
    生成某日某时段菜单
     */
    fun createDailyMeal(newDailyMeal: NewDailyMeal) {
        launchUI {
            withContext(Dispatchers.IO) {
                repository.createDailyMeal(newDailyMeal)
            }
        }
    }
}