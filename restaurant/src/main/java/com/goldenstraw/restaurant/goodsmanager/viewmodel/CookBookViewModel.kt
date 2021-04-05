package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.None
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.Success
import com.goldenstraw.restaurant.goodsmanager.utils.CookKind.*
import com.goldenstraw.restaurant.goodsmanager.utils.MealTime
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.network.ResponseThrowable
import com.owner.basemodule.network.parserResponse
import com.owner.basemodule.room.entities.*
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


    val viewState = ObservableField<Int>()

    var mealTime = MealTime.Breakfast.time

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

    val materialList = mutableListOf<Material>()

    var cookbookList = mutableListOf<CookBookWithMaterials>()


    //对菜单中的菜谱分组，获得菜谱使用次数.key-value中key为菜谱名，value为次数
    var cookbookByNameAndNumber = hashMapOf<String, Int>()

    /*
     一、 增加菜谱，首先保存菜谱到网络后，得到它的objectId,然后遍历它的原材料，
     将原材料的materialOwnerId设为objectId。然后再保存。
     这是个长时间运行过程，只有它完成了，才能显示在UI上。它运行异步任务中，所以不会堵塞主线程，
     就会影响显示结果。
     */
    fun createCookBook(newCookBook: RemoteCookBook) {
        launchUI( {
            newCookBook.save(object : SaveListener<String>() {
                override fun done(id: String?, e: BmobException?) {
                    if (e == null) {
                        newCookBook.material.forEach {
                            it.materialOwnerId = id!!
                        }
                        viewModelScope.launch {
                            repository.addCookBookToLocal(
                                remoteToLocalCookBook(
                                    newCookBook
                                )
                            )
                            repository.addMaterialOfCookBooks(newCookBook.material as MutableList<Material>)
                            defUI.refreshEvent.call()
                        }

                        newCookBook.update(object : UpdateListener() {
                            override fun done(p0: BmobException?) {
                                if (p0 == null) {
                                    defUI.showDialog.value = "菜谱添加成功！！"
                                    materialList.clear()
                                }
                            }
                        })
                    } else {
                        defUI.showDialog.value = e.message
                    }
                }
            })
        },{
            defUI.showDialog.value = it.message
        })


    }

    /*
    二、删除.成功后，删除对应的关系
     */
    fun deleteCookBook(cm: CookBookWithMaterials) {

        launchUI ({
            parserResponse(repository.deleteRemoteCookBook(cm.cookbook.objectId)) {
                repository.deleteLocalCookBookWithMaterials(cm)
                defUI.showDialog.value = "删除成功"
            }

        },{
            defUI.showDialog.value = (it as ResponseThrowable).errMsg
        })
    }

    suspend fun deleteMaterialOfCookBook(material: MutableList<Material>) {
        repository.deleteMaterialsOfCookBook(material)
    }

    /*
       修改菜谱
     */
    suspend fun updateCookBook(cookbook: LocalCookBook, material: List<Material>) {
        val remoteCookBook = localToRemoteCookBook(cookbook, material)
        remoteCookBook.update(cookbook.objectId, object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {

                } else {
                    throw p0  //将异常抛出
                }
            }
        })
        repository.addCookBookToLocal(cookbook)
        repository.addMaterialOfCookBooks(material as MutableList<Material>)

    }

    /*
    查询，对结果通过groupBy进行分组。
     */

    //分组列表，key-value:key为小类，value为内容列表
    var groupbyKind = hashMapOf<String, MutableList<CookBookWithMaterials>>()

    suspend fun getCookBookWithMaterialsOfCategory(category: String, isStandby: Boolean) {
        groupbyKind.clear()
        cookbookList = repository.getCookBookWithMaterialOfCategory(category, isStandby)

//        //这个判断主要是用于初始使用，如果本地没有数据则从网络获取，然后，再次从本地查询。
//
//        if (cookbookList.isNullOrEmpty()) {
////            asyncCookBooks(category)
//            cookbookList = repository.getCookBookWithMaterialOfCategory(category, isStandby)
//        }
        Observable.fromIterable(cookbookList)
            .groupBy { cookBookWithMaterials ->
                cookBookWithMaterials.cookbook.foodKind
            }
            .autoDisposable(this@CookBookViewModel)
            .subscribe { group ->
                groupbyKind[group.key!!] = mutableListOf()//为每个分类建立key-value值
                group
                    .autoDisposable(this@CookBookViewModel)
                    .subscribe { cookbooks ->
                        groupbyKind[group!!.key]!!.add(cookbooks)//将对应分类的菜谱，存入对应的列表中
                    }
            }


    }


    /*
    从本地数据中查找原材料（主要因为Bomb模糊查询要收费）
    启动一个协程，在这个协程里调用挂起函数对数据库进行访问，并返回结果
     */
    fun searchMaterial(name: String) {
        launchUI ({
            val goods = repository.searchMaterial(name)
            if (goods.isEmpty()) {
                searchedGoodsStatusLiveData.value = None
            } else {
                searchedGoodsStatusLiveData.value = Success(goods)
            }
        },{
            defUI.showDialog.value = it.message
        })
    }

    /*
    从本地模糊查询菜谱
     */
    fun searchCookBookWithMaterials(name: String, category: String) {

        launchUI( {
            val list = repository.searchCookBook(name, category)
            if (list.isEmpty()) {
                searchCookbookStatusLiveDate.value = None
            } else {
                searchCookbookStatusLiveDate.value = Success(list)
            }

        },{
            defUI.showDialog.value = it.message
        })
    }

    /*
     * 修改远程菜谱使用次数。
     *
     */
    private suspend fun updateNumberOfUsed(cookbook: RemoteCookBook) {
        val newNumber = UpdateUsedNumber(cookbook.usedNumber)
        cookbook.objectId?.let {
            parserResponse(repository.updateNumberOfUsed(newNumber, it)) {
                repository.addCookBookToLocal(remoteToLocalCookBook(cookbook))
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
    private fun clearAllList() {
        coldList.clear()
        hotList.clear()
        flourList.clear()
        soupList.clear()
        snackList.clear()
    }

    //分析菜单位的统计结果
    private var _analyzeResult = MutableLiveData<AnalyzeMealResult>()

    val analyzeResult: LiveData<AnalyzeMealResult> = _analyzeResult

    //统计分析菜谱,将菜单按冷菜，热菜，主食，汤粥，明档等大类细分出它们中的小类，如：素菜，小荤菜等。
//并统计小类的数量
    fun statistcsDailyMeal(list: MutableList<String>) {

        launchUI ({
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

        },{
            defUI.showDialog.value = it.message
        })
    }

    private fun getNumber(list: MutableList<DailyMeal>, kind: String): Int {
        var shuliang = 0
        list.forEach {
            if (it.cookBook.foodKind == kind)
                shuliang++
        }
        return shuliang
    }


    /*
    获取日期范围的菜单，并分组到大类列表对象中
    @list为日期列表
     */
    private suspend fun getRangeOfDateFromDailyMeal(dateList: MutableList<String>) {

        val allDailyMeal = mutableListOf<DailyMeal>()

        withContext(Dispatchers.IO) {
            allDailyMeal.clear()
            dateList.forEach { date ->
                val where = "{\"mealDate\":\"$date\"}"
                parserResponse(repository.getDailyMealOfDate(where)) {
                    allDailyMeal.addAll(it)
                }
            }
            groupByKindForDailyMeal(allDailyMeal)
        }


    }

    /*
      从已经获取的某期间，某大类菜品列表当中包含的某小类菜谱的使用次数
      @category:大类
      @kind:小类
     */
    fun getCookBooksOfDailyMeal(category: String, kind: String) {
        launchUI( {
            when (category) {
                ColdFood.kindName -> {
                    getMapOfCookBook(coldList, kind)
                }
                HotFood.kindName -> {
                    getMapOfCookBook(hotList, kind)
                }
                FlourFood.kindName -> {
                    getMapOfCookBook(flourList, kind)
                }
                SoutPorri.kindName -> {
                    getMapOfCookBook(soupList, kind)
                }
                Snackdetail.kindName -> {
                    getMapOfCookBook(snackList, kind)
                }

            }

        },{
            defUI.showDialog.value = it.message
        })
    }

    private fun getMapOfCookBook(list: MutableList<DailyMeal>, kind: String) {
        cookbookByNameAndNumber.clear()
        Observable.fromIterable(list)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter {
                it.cookBook.foodKind == kind
            }

            .autoDisposable(this@CookBookViewModel)
            .subscribe({ dailymeal ->
                if (!cookbookByNameAndNumber.containsKey(dailymeal.cookBook.foodName)) {
                    cookbookByNameAndNumber[dailymeal.cookBook.foodName] = 1
                } else {
                    val count = cookbookByNameAndNumber[dailymeal.cookBook.foodName]!! + 1
                    cookbookByNameAndNumber[dailymeal.cookBook.foodName] = count
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

        launchUI({
            viewState.set(MultiStateView.VIEW_STATE_LOADING)
            parserResponse(repository.getDailyMealOfDate(where)) {
                groupByKindForDailyMeal(it)
            }
            _refreshAdapter.value = "All"
            viewState.set(MultiStateView.VIEW_STATE_CONTENT)

        }, {
            viewState.set(MultiStateView.VIEW_STATE_ERROR)
            defUI.showDialog.value = it.message
        })
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
    拷贝某一天菜单,
     */
    fun copyDailyMeal(newDate: String, oldDate: String, direct: Int) {
        launchUI ({
            val where = "{\"mealDate\":\"$oldDate\"}"
            withContext(Dispatchers.IO) {
                parserResponse(repository.getDailyMealOfDate(where)) {
                    Observable.fromIterable(it)
                        .map { oldDailyMeal ->
                            //菜谱使用次数加1
                            oldDailyMeal.cookBook.usedNumber = +1

                            val newDailyMeal = NewDailyMeal(
                                oldDailyMeal.mealTime,
                                newDate,
                                oldDailyMeal.cookBook,
                                oldDailyMeal.isOfTeacher,
                                direct
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
                }
            }

        },{
            defUI.showDialog.value = (it as ResponseThrowable).errMsg
        })
    }

    fun updateDailyMeal(newDailyMeal: UpdateIsteacher, objectId: String) {
        launchUI ({
            parserResponse(repository.updateDailyMeal(newDailyMeal, objectId))
        },{
            defUI.showDialog.value =  (it as ResponseThrowable).errMsg
        })
    }

    /*
     * 删除一日菜单
     */
    fun deleteDailyMealOfDate(date: String, direct: Int) {
        launchUI( {
            val where = "{\"\$and\":[{\"mealDate\":\"$date\"},{\"direct\":$direct}]}"
            parserResponse(repository.getDailyMealOfDate(where)) {
                it.forEach { meal ->
                    deleteDailyMeal(meal)
                }
                clearAllList()
                _refreshAdapter.value = "All"
            }
        },{
            defUI.showDialog.value =  (it as ResponseThrowable).errMsg
        })

    }

    /*
     * 删除某项菜单,同时将菜谱使用次数减1
     */
    fun deleteDailyMeal(dailyMeal: DailyMeal) {
        launchUI ({
            val cookbook = repository.getCookbook(dailyMeal.cookBook.objectId)
            cookbook.usedNumber = cookbook.usedNumber - 1
            withContext(Dispatchers.IO) {
                parserResponse(repository.deleteDailyMeal(dailyMeal.objectId)) {
                    updateNumberOfUsed(cookbook)
                }
            }

        },{
            defUI.showDialog.value =  (it as ResponseThrowable).errMsg
        })
    }

    /*
      生成某日某时段菜单，同时将菜谱使用次数加1
      因为菜单和菜谱没有建立关联关系，菜单中的菜谱使用数量是原始的，
      所以需要从菜谱库中获取最近的菜谱，然后修改数量。
     */
    fun createDailyMeal(newDailyMeal: NewDailyMeal) {
        launchUI ({
            withContext(Dispatchers.IO) {
                parserResponse(repository.createDailyMeal(newDailyMeal)) {
                    //获取最新菜谱使用数量，增加
                    val cookbook = repository.getCookbook(newDailyMeal.cookBook.objectId)
                    cookbook.usedNumber = cookbook.usedNumber + 1
                    updateNumberOfUsed(cookbook)
                }

            }
        },{
            defUI.showDialog.value =  (it as ResponseThrowable).errMsg
        })
    }

    /***********************************************************
     *将每日菜单转换成Word表格
     ***********************************************************/
    //定义三个Map变量，分别对应早，中，晚三餐
    private val breakfast = mutableListOf<LocalCookBook>()
    private val lunch = mutableListOf<LocalCookBook>()
    private val dinner = mutableListOf<LocalCookBook>()

    fun createStyledTable(date: String, file: InputStream) {
        launchUI( {
            breakfast.clear()
            lunch.clear()
            dinner.clear()
            withContext(Dispatchers.IO) {
                val where = "{\"mealDate\":\"$date\"}"
                parserResponse(repository.getDailyMealOfDate(where)) { list ->
                    Observable.fromIterable(list)
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
                }
            }
            withContext(Dispatchers.IO) {
                createOutFileOfWord(date, file)
            }
            defUI.showDialog.value = "文件生成完毕！！"
        },{
            defUI.showDialog.value =  (it as ResponseThrowable).errMsg
        })

    }


    private fun createOutFileOfWord(date: String, file: InputStream) {

        val doc = HWPFDocument(file)
        val range = doc.range
        range.replaceText("\${cookbookDate}", date)
        range.replaceText("\${breakfast_cold}", getCookBook(ColdFood.kindName, breakfast))
        range.replaceText("\${breakfast_hot}", getCookBook(HotFood.kindName, breakfast))
        range.replaceText(
            "\${breakfast_flour}",
            getCookBook(FlourFood.kindName, breakfast)
        )
        range.replaceText("\${breakfast_soup}", getCookBook(SoutPorri.kindName, breakfast))
        range.replaceText(
            "\${breakfast_snack}",
            getCookBook(Snackdetail.kindName, breakfast)
        )
        range.replaceText("\${lunch_cold}", getCookBook(ColdFood.kindName, lunch))
        range.replaceText("\${lunch_hot}", getCookBook(HotFood.kindName, lunch))
        range.replaceText("\${lunch_flour}", getCookBook(FlourFood.kindName, lunch))
        range.replaceText("\${lunch_soup}", getCookBook(SoutPorri.kindName, lunch))
        range.replaceText("\${lunch_snack}", getCookBook(Snackdetail.kindName, lunch))
        range.replaceText("\${dinner_cold}", getCookBook(ColdFood.kindName, dinner))
        range.replaceText("\${dinner_hot}", getCookBook(HotFood.kindName, dinner))
        range.replaceText("\${dinner_flour}", getCookBook(FlourFood.kindName, dinner))
        range.replaceText("\${dinner_soup}", getCookBook(SoutPorri.kindName, dinner))
        range.replaceText("\${dinner_snack}", getCookBook(Snackdetail.kindName, dinner))
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
    private fun getCookBook(cookkind: String, cookbooksList: MutableList<LocalCookBook>): String {

        return when (cookkind) {
            ColdFood.kindName -> {
                var coldfood = ""
                cookbooksList.forEach {
                    if (it.foodCategory == ColdFood.kindName) {
                        coldfood += it.foodName + " "

                    }
                }
                return coldfood
            }
            HotFood.kindName -> {
                var hotfood = ""
                cookbooksList.forEach {
                    if (it.foodCategory == HotFood.kindName) {
                        hotfood += it.foodName + " "
                    }
                }
                return hotfood
            }
            FlourFood.kindName -> {
                var flourfood = ""
                cookbooksList.forEach {
                    if (it.foodCategory == FlourFood.kindName) {
                        flourfood += it.foodName + " "
                    }
                }
                return flourfood
            }
            SoutPorri.kindName -> {
                var soutporri = ""
                cookbooksList.forEach {
                    if (it.foodCategory == SoutPorri.kindName) {
                        soutporri += it.foodName + " "
                    }
                }
                return soutporri
            }
            Snackdetail.kindName -> {
                var snackdetail = ""
                cookbooksList.forEach {
                    if (it.foodCategory == Snackdetail.kindName) {
                        snackdetail += it.foodName + " "
                    }
                }
                return snackdetail
            }
            else -> return ""
        }
    }


    /********************************************************************************
     * 同步数据，主要是因为网络数据可以被不同的人修改，所以在查看菜谱时需要将网络数据与本地数据进行同步。
     * 先读取本地数据，然后读取网络
     ********************************************************************************/

    fun syncCookbook() {
        launchUI ({
            var skip = 0
            val cookbooksList = mutableListOf<RemoteCookBook>()
            var isCompleted = false
            while (!isCompleted) {
                parserResponse(repository.getAllOfRemoteCookBook(skip)) {
                    cookbooksList.addAll(it)
                    if (it.size == 200)//如果结果列表等于500，说明可能还有数据，则继续
                        skip += 200
                    else //如果列表数量小于500，说明已经是最后一页了。
                        isCompleted = true

                }
            }
            //清空本地数据
            repository.clearLocalCookBook()
            //远程数据转换为本地数据
            val localCookBook = mutableListOf<LocalCookBook>()
            val materials = mutableListOf<Material>()
            cookbooksList.forEach {
                localCookBook.add(remoteToLocalCookBook(it))
                materials.addAll(it.material)
            }
            //保存本地数据
            repository.addCookBooksToLocal(localCookBook)
            repository.addMaterialOfCookBooks(materials)
        },{
            defUI.showDialog.value =  (it as ResponseThrowable).errMsg
        })
    }
}
