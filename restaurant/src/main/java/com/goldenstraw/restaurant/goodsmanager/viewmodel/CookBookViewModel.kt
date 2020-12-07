package com.goldenstraw.restaurant.goodsmanager.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.CountListener
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.None
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.Success
import com.goldenstraw.restaurant.goodsmanager.utils.CookKind
import com.goldenstraw.restaurant.goodsmanager.utils.CookKind.*
import com.goldenstraw.restaurant.goodsmanager.utils.MealTime
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.CBGCrossRef
import com.owner.basemodule.room.entities.CookBookWithGoods
import com.owner.basemodule.room.entities.CookBooks
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.util.ReturnResult
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import org.apache.poi.hwpf.HWPFDocument
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 * 使用协程。对菜谱的管理（增删），对每日菜单的管理（增改）
 */
class CookBookViewModel(
    private val repository: CookBookRepository
) : BaseViewModel() {

    /**
     * 对菜谱的管理部分
     */
    val searchedGoodsStatusLiveData = MutableLiveData<SearchedStatus>(None)
    val searchCookbookStatusLiveDate = MutableLiveData<SearchedStatus>(None)


    /*
      共享变量：菜谱原材料列表。当在查询商品的列表中选择某一个原材料后，将该材料加入原材料列表中；
      保存菜谱时从这里获取商品列表信息。
      通过这种共享方式实现SearchMaterialFragment中的选择，显示在InputCookBookFragment中。
    */

    val materialList = mutableListOf<Goods>()
    var cookbookList = mutableListOf<CookBookWithGoods>()

    //分组列表，key-value:key为小类，value为内容列表
    var groupbyKind = hashMapOf<String, MutableList<CookBookWithGoods>>()

    //对菜单中的菜谱分组，获得菜谱使用次数.key-value中key为菜谱名，value为次数
    var cookbookByNameAndNumber = hashMapOf<String, Int>()

    /*
     一、 增加菜谱，首先保存菜谱到网络后，得到它的objectId,然后根据原材料列表中的每个原材料，
         创建关联关系对象，保存到网络和本地。
     */
    fun createCookBook(newCookBook: NewCookBook) {
        launchUI {
            when (val result = repository.createCookBook(newCookBook)) {
                is ReturnResult.Success<*> -> {
                    materialList.forEach { goods ->
                        val newCrossRef =
                            NewCrossRef(
                                (result.value as CookBooks).objectId,
                                goods.objectId,
                                (result.value as CookBooks).foodCategory
                            )
                        when (val ref = repository.createCrossRef(newCrossRef)) {
                            is ReturnResult.Failure -> defUI.showDialog.postValue(ref.e)//失败提示
                        }
                    }
                    materialList.clear()
//                    defUI.refreshEvent.call()
                }
                is ReturnResult.Failure -> {
                    defUI.showDialog.postValue(result.e)
                }
            }
        }

    }

    /*
    二、删除.成功后，删除对应的关系
     */
    fun deleteCookBook(cookBooks: CookBooks) {
        launchUI {
            if (repository.deleteCookBook(cookBooks.objectId).isSuccess()) {
                val where = "{\"cb_id\":\"${cookBooks.objectId}\"}"
                val refList = repository.getCookBookGoodsCrossRef(where, 0)
                withContext(Dispatchers.IO) {
                    refList.results?.forEach { ref ->
                        repository.deleteCrossRef(ref.objectId)
                    }
                }
                repository.deleteLocalCookBookAndRef(cookBooks)
            }
        }
    }

    /*
    查询，对结果通过groupBy进行分组。
     */
    fun getCookBookWithGoodsOfCategory(category: String) {
        launchUI {
            groupbyKind.clear()
            cookbookList = repository.getCookBookWithGoodsOfCategory(category)
            Observable.fromIterable(cookbookList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .groupBy { cookbooks ->
                    cookbooks.cookBook.foodKind
                }
                .autoDisposable(this@CookBookViewModel)
                .subscribe({ group ->
                    groupbyKind[group.key!!] = mutableListOf()//为每个分类建立key-value值
                    group.autoDisposable(this@CookBookViewModel)
                        .subscribe { cookbooks ->
                            groupbyKind[group!!.key]!!.add(cookbooks)//将对应分类的菜谱，存入对应的列表中
                        }
                }, {}, {
                    defUI.refreshEvent.call()//发出刷新数据通知
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
                searchedGoodsStatusLiveData.value = None
            } else {
                searchedGoodsStatusLiveData.value = Success(goods)
            }
        }
    }

    fun searchCookBookWithGoods(name: String, category: String) {
        launchUI {
            val list = repository.searchCookBook(name, category)
            if (list.isEmpty()) {
                searchCookbookStatusLiveDate.value = None
            } else {
                searchCookbookStatusLiveDate.value = Success(list)
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

    //分析菜单位的统计结果a
    private var _analyzeResult = MutableLiveData<AnalyzeMealResult>()

    val analyzeResult: LiveData<AnalyzeMealResult> = _analyzeResult

    //统计分析菜谱
    fun statistcsDailyMeal(list: MutableList<String>) {

        launchUI {
            getRangeOfDateFromDailyMeal(list)

            val cold_sucai = async {
                getNumber(coldList, "素菜")
            }
            val cold_xiaohun = async { getNumber(coldList, "小荤菜") }
            val cold_dahun = async { getNumber(coldList, "大荤菜") }
            val hot_sucai = async { getNumber(hotList, "素菜") }
            val hot_xiaohun = async { getNumber(hotList, "小荤菜") }
            val hot_dahun = async { getNumber(hotList, "大荤菜") }
            val flour_mianshi = async { getNumber(flourList, "面食") }
            val flour_xianlei = async { getNumber(flourList, "馅类") }
            val flour_zaliang = async { getNumber(flourList, "杂粮") }
            val soup_tang = async { getNumber(soupList, "汤") }
            val soup_zhou = async { getNumber(soupList, "粥") }
            val snack_zhu = async { getNumber(snackList, "煮") }
            val snack_jianchao = async { getNumber(snackList, "煎炒") }
            val snack_youzha = async { getNumber(snackList, "油炸") }
            _analyzeResult.value = AnalyzeMealResult(
                cold_sucai.await(), cold_xiaohun.await(), cold_dahun.await(),
                hot_sucai.await(), hot_xiaohun.await(), hot_dahun.await(),
                flour_mianshi.await(), flour_xianlei.await(), flour_zaliang.await(),
                soup_tang.await(), soup_zhou.await(),
                snack_zhu.await(), snack_jianchao.await(), snack_youzha.await()
            )

        }
    }

    private fun getNumber(list: MutableList<DailyMeal>, kind: String): Int {
        var shuliang = 0
        list.forEach {
            if (it.cookBook.foodKind == kind)
                shuliang++
        }
        return shuliang
    }


    //获取日期范围的菜单，并分组
    suspend fun getRangeOfDateFromDailyMeal(list: MutableList<String>) {
        val allDailyMeal = mutableListOf<DailyMeal>()

        withContext(Dispatchers.IO) {
            allDailyMeal.clear()
            list.forEach { date ->
                val where = "{\"mealDate\":\"$date\"}"
                val objectList = repository.getDailyMealOfDate(where)
                if (objectList.isSuccess()) {
                    allDailyMeal.addAll(objectList.results!!)
                } else {
                    defUI.showDialog.value = objectList.error
                }
            }
            groupByKindForDailyMeal(allDailyMeal)
        }


    }

    //统计某类菜品的所有菜谱及使用次数
    fun getCookBooksOfDailyMeal(category: String, kind: String) {
        launchUI {
            when (category) {
                ColdFood.kindName -> {
                    getHashMapOfCookBook(coldList, kind)
                }
                HotFood.kindName -> {
                    getHashMapOfCookBook(hotList, kind)
                }
                FlourFood.kindName -> {
                    getHashMapOfCookBook(flourList, kind)
                }
                SoutPorri.kindName -> {
                    getHashMapOfCookBook(soupList, kind)
                }
                Snackdetail.kindName -> {
                    getHashMapOfCookBook(snackList, kind)
                }

            }

        }
    }

    private fun getHashMapOfCookBook(list: MutableList<DailyMeal>, kind: String) {
        Observable.fromIterable(list)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter {
                it.cookBook.foodKind == kind
            }
            .groupBy { cookbooks ->
                cookbooks.cookBook.foodName
            }
            .autoDisposable(this@CookBookViewModel)
            .subscribe({ group ->
                cookbookByNameAndNumber[group.key!!] = 1//为每个分类建立key-value值
                group.autoDisposable(this@CookBookViewModel)
                    .subscribe {
                        cookbookByNameAndNumber[group.key!!] = +1
                    }
            }, {

            }, {
                defUI.refreshEvent.call()
            })
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
            withContext(Dispatchers.IO) {
                val objectList = repository.getDailyMealOfDate(where)
                if (objectList.isSuccess()) {
                    groupByKindForDailyMeal(objectList.results!!)

                } else {
                    defUI.showDialog.value = objectList.error
                }
            }
            _refreshAdapter.value = "All"

        }
    }


    //对每日菜单进行分组
    private fun groupByKindForDailyMeal(list: MutableList<DailyMeal>) {
        clearAllList()//先清空列表，然后将新结果加入
        Observable.fromIterable(list)
            .groupBy { meal ->
                meal.cookBook.foodCategory
            }
            .autoDisposable(this@CookBookViewModel)
            .subscribe { group ->
                group.autoDisposable(this@CookBookViewModel)
                    .subscribe { dailies ->
                        when (group.key) {
                            ColdFood.kindName -> coldList.add(dailies)
                            HotFood.kindName -> hotList.add(dailies)
                            FlourFood.kindName -> flourList.add(dailies)
                            SoutPorri.kindName -> soupList.add(dailies)
                            Snackdetail.kindName -> snackList.add(dailies)
                        }
                    }
            }
    }

    /*
    拷贝某一天菜单
     */
    fun copyDailyMeal(newDate: String, oldDate: String) {
        launchUI {
            val where = "{\"mealDate\":\"$oldDate\"}"
            val objectList = repository.getDailyMealOfDate(where)
            withContext(Dispatchers.Default) {
                if (objectList.isSuccess()) {
                    Observable.fromIterable(objectList.results)
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
                            val where1 =
                                "{\"\$and\":[{\"mealTime\":\"${MealTime.Breakfast.time}\"}" +
                                        ",{\"mealDate\":\"$newDate\"}]}"
                            getDailyMealOfDate(where1)
                        })
                } else {
                    defUI.showDialog.value = objectList.error
                }
            }

        }
    }

    fun updateDailyMeal(newDailyMeal: UpdateIsteacher, objectId: String) {
        launchUI {
            repository.updateDailyMeal(newDailyMeal, objectId)
        }
    }

    /*
     * 删除一日菜单
     */
    fun deleteDailyMealOfDate(date: String) {
        launchUI {

            val where = "{\"mealDate\":\"$date\"}"
            val objectList = repository.getDailyMealOfDate(where)
            if (objectList.isSuccess()) {
                objectList.results?.forEach { meal ->
                    deleteDailyMeal(meal.objectId)
                }
            } else {
                defUI.showDialog.value = objectList.error
            }
            clearAllList()
            _refreshAdapter.value = "All"

        }

    }

    /*
     * 删除某项菜单
     */
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

    /**
     *将每日菜单转换成Word表格
     */
//定义三个Map变量，分别对应早，中，晚三餐
    private val breakfast = mutableListOf<CookBooks>()
    private val lunch = mutableListOf<CookBooks>()
    private val dinner = mutableListOf<CookBooks>()

    fun createStyledTable(date: String, file: InputStream) {
        launchUI {
            breakfast.clear()
            lunch.clear()
            dinner.clear()
            withContext(Dispatchers.Default) {
                val where = "{\"mealDate\":\"$date\"}"
                val objectList = repository.getDailyMealOfDate(where)
                if (objectList.isSuccess()) {
                    Observable.fromIterable(objectList.results)
                        .groupBy {
                            it.mealTime
                        }.subscribeOn(Schedulers.io())
                        .autoDisposable(this@CookBookViewModel)
                        .subscribe { group ->
                            group.autoDisposable(this@CookBookViewModel)
                                .subscribe { meal ->
                                    when (group.key) {
                                        MealTime.Breakfast.time -> {
                                            breakfast.add(meal.cookBook)
                                        }
                                        MealTime.Lunch.time -> {
                                            lunch.add(meal.cookBook)
                                        }
                                        MealTime.Dinner.time -> {
                                            dinner.add(meal.cookBook)
                                        }
                                        else -> {
                                        }
                                    }
                                }
                        }
                } else {
                    defUI.showDialog.value = objectList.error
                }
            }
            withContext(Dispatchers.IO) {
                createOutFileOfWord(date, file)
            }
            defUI.showDialog.value = "文件生成完毕！！"
        }

    }


    private fun createOutFileOfWord(date: String, file: InputStream) {

        val doc = HWPFDocument(file)
        val range = doc.range
        range.replaceText("\${cookbookDate}", date)
        range.replaceText("\${breakfast_cold}", getCookBook(CookKind.ColdFood.kindName, breakfast))
        range.replaceText("\${breakfast_hot}", getCookBook(CookKind.HotFood.kindName, breakfast))
        range.replaceText(
            "\${breakfast_flour}",
            getCookBook(CookKind.FlourFood.kindName, breakfast)
        )
        range.replaceText("\${breakfast_soup}", getCookBook(CookKind.SoutPorri.kindName, breakfast))
        range.replaceText(
            "\${breakfast_snack}",
            getCookBook(CookKind.Snackdetail.kindName, breakfast)
        )
        range.replaceText("\${lunch_cold}", getCookBook(CookKind.ColdFood.kindName, lunch))
        range.replaceText("\${lunch_hot}", getCookBook(CookKind.HotFood.kindName, lunch))
        range.replaceText("\${lunch_flour}", getCookBook(CookKind.FlourFood.kindName, lunch))
        range.replaceText("\${lunch_soup}", getCookBook(CookKind.SoutPorri.kindName, lunch))
        range.replaceText("\${lunch_snack}", getCookBook(CookKind.Snackdetail.kindName, lunch))
        range.replaceText("\${dinner_cold}", getCookBook(CookKind.ColdFood.kindName, dinner))
        range.replaceText("\${dinner_hot}", getCookBook(CookKind.HotFood.kindName, dinner))
        range.replaceText("\${dinner_flour}", getCookBook(CookKind.FlourFood.kindName, dinner))
        range.replaceText("\${dinner_soup}", getCookBook(CookKind.SoutPorri.kindName, dinner))
        range.replaceText("\${dinner_snack}", getCookBook(CookKind.Snackdetail.kindName, dinner))
        val outfile = File("/storage/emulated/0/$date.doc")
        val outFile = FileOutputStream(outfile)
        doc.write(outFile)
        try {
            file.close()
            outFile.close()
        } catch (e: IOException) {
            defUI.showDialog.value = e.message
        }


    }

    /*
    获取菜谱
     */
    private fun getCookBook(cookkind: String, cookbooksList: MutableList<CookBooks>): String {

        return when (cookkind) {
            CookKind.ColdFood.kindName -> {
                var coldfood = ""
                cookbooksList.forEach {
                    if (it.foodCategory == CookKind.ColdFood.kindName) {
                        coldfood += it.foodName + " "

                    }
                }
                return coldfood
            }
            CookKind.HotFood.kindName -> {
                var hotfood = ""
                cookbooksList.forEach {
                    if (it.foodCategory == CookKind.HotFood.kindName) {
                        hotfood += it.foodName + " "
                    }
                }
                return hotfood
            }
            CookKind.FlourFood.kindName -> {
                var flourfood = ""
                cookbooksList.forEach {
                    if (it.foodCategory == CookKind.FlourFood.kindName) {
                        flourfood += it.foodName + " "
                    }
                }
                return flourfood
            }
            CookKind.SoutPorri.kindName -> {
                var soutporri = ""
                cookbooksList.forEach {
                    if (it.foodCategory == CookKind.SoutPorri.kindName) {
                        soutporri += it.foodName + " "
                    }
                }
                return soutporri
            }
            CookKind.Snackdetail.kindName -> {
                var snackdetail = ""
                cookbooksList.forEach {
                    if (it.foodCategory == CookKind.Snackdetail.kindName) {
                        snackdetail += it.foodName + " "
                    }
                }
                return snackdetail
            }
            else -> return ""
        }
    }


    /**
     * 同步数据，主要是因为网络数据可以被不同的人修改，所以在查看菜谱时需要将网络数据与本地数据进行同步。
     * 先读取本地数据，然后读取网络
     */
    fun asyncCookBooks(category: String) {
        var skip = 0
        val query = BmobQuery<CookBooks>()
        query.addWhereEqualTo("foodCategory", category)
        query.count(CookBooks::class.java, object : CountListener() {
            override fun done(count: Int?, e: BmobException?) {
                if (e == null) {
                    launchUI {
                        val f = count!! / 500  //分页，500条为一页
                        val where = "{\"foodCategory\":\"$category\"}"
                        val cookbookList = async {
                            val allcookbook: MutableList<CookBooks> = mutableListOf()
                            for (a in 0..f) {
                                allcookbook.addAll(
                                    repository.getCookBookOfCategory(where, skip).results!!
                                )
                                skip += 500
                            }
                            allcookbook
                        }
                        repository.clearCookBookOfCategory(category)

                        repository.addCookBookToLocal(cookbookList.await())

                    }
                } else {
                    defUI.showDialog.postValue(e.message)
                }
            }

        })

    }

    /**
     * 分页，一次只能获取500条数据，所以先判数据总数，然后进行分页循环，得到所有数据后，清空本地数据，然后保存新数据
     */
    fun asyncCrossRefs(category: String) {
        var skip = 0
        val query = BmobQuery<CBGCrossRef>()
        query.addWhereEqualTo("foodCategory", category)
        query.count(CBGCrossRef::class.java, object : CountListener() {
            override fun done(count: Int?, e: BmobException?) {
                if (e == null) {
                    launchUI {
                        val f = count!! / 500
                        val where = "{\"foodCategory\":\"$category\"}"
                        val crossRefList = async {
                            val allRef: MutableList<CBGCrossRef> = mutableListOf()
                            for (a in 0..f) {
                                allRef.addAll(
                                    repository.getCookBookGoodsCrossRef(
                                        where = where,
                                        skip = skip
                                    ).results!!

                                )
                                skip += 500
                            }
                            allRef
                        }
                        repository.clearCrossRefOfCategory(category)

                        repository.addCrossRefToLocal(crossRefList.await())

                    }
                } else {
                    defUI.showDialog.postValue(e.message)
                }
            }

        })


    }

}