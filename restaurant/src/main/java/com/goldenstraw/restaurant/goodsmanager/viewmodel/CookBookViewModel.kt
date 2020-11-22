package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.CountListener
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.None
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.Success
import com.goldenstraw.restaurant.goodsmanager.utils.CookKind
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER
import org.apache.poi.xwpf.usermodel.ParagraphAlignment.LEFT
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.xmlbeans.impl.richParser.XMLStreamReaderExt
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc
import java.io.FileOutputStream
import java.math.BigInteger

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


    /*
     一、 增加菜谱，首先保存菜谱到网络后，得到它的objectId,然后根据原材料列表中的每个原材料，
         创建关联关系对象，保存到网络和本地。
     */
    suspend fun createCookBook(newCookBook: NewCookBook) {
        launchFlow {
            repository.createCookBook(newCookBook)
        }.collect { result ->
            when (result) {
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
        viewModelScope.launch {
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
    suspend fun getCookBookWithGoodsOfCategory(category: String) {
        launchFlow {
            repository.getCookBookWithGoodsOfCategory(category)
        }.onStart {
            groupbyKind.clear()
        }.collect {
            cookbookList = it
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
            val cookbookList = repository.searchCookBook(name, category)
            if (cookbookList.isEmpty()) {
                searchCookbookStatusLiveDate.value = None
            } else {
                searchCookbookStatusLiveDate.value = Success(cookbookList)
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
    fun copyDailyMeal(newDate: String, oldDate: String) {
        launchUI {
            launchFlow {
                val where = "{\"mealDate\":\"$oldDate\"}"
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

    /*
     *将每日菜单转换成Word表格
     */
    //定义三个Map变量，分别对应早，中，晚三餐
    val breakfast = mutableListOf<CookBooks>()
    val lunch = mutableListOf<CookBooks>()
    val dinner = mutableListOf<CookBooks>()

    suspend fun createStyledTable(date: String) {
        launchFlow {
            val where = "{\"mealDate\":\"$date\"}"
            repository.getDailyMealOfDate(where)
        }.flowOn(Dispatchers.IO)
            .onCompletion {
                createOutFile(date)
                defUI.showDialog.value = "文件生成完毕！！"
            }
            .collect {
                if (it.isSuccess()) {
                    Observable.fromIterable(it.results)
                        .groupBy {
                            it.mealTime
                        }.subscribeOn(Schedulers.io())
                        .autoDisposable(this)
                        .subscribe { group ->
                            group.autoDisposable(this)
                                .subscribe { meal ->
                                    when (group.key) {
                                        MealTime.Breakfast.time ->
                                            breakfast?.add(meal.cookBook)
                                        MealTime.Lunch.time ->
                                            lunch?.add(meal.cookBook)
                                        MealTime.Dinner.time ->
                                            dinner?.add(meal.cookBook)
                                    }
                                }
                        }
                } else {
                    defUI.showDialog.value = it.error
                }
            }

    }

    /*
    获取菜谱
     */
    private fun getCookBook(cookkind: String, cookbooksList: MutableList<CookBooks>): String {

        return when (cookkind) {
            CookKind.ColdFood.kindName -> {
                var coldfood = ""
                var i = 0
                cookbooksList.forEach {
                    if (it.foodCategory == CookKind.ColdFood.kindName) {
                        coldfood += it.foodName + " "
                        i++
                    }
                    if (i % 4 == 0) {
                        coldfood += "\n"
                    }

                }
                return coldfood
            }
            CookKind.HotFood.kindName -> {
                var hotfood = ""
                var i = 0
                cookbooksList.forEach {
                    if (it.foodCategory == CookKind.HotFood.kindName) {
                        hotfood += it.foodName + " "
                        i++
                    }
                    if (i % 4 == 0) {
                        hotfood += "\n"
                    }

                }
                return hotfood
            }
            CookKind.FlourFood.kindName -> {
                var flourfood = ""
                var i = 0
                cookbooksList.forEach {
                    if (it.foodCategory == CookKind.FlourFood.kindName) {
                        flourfood += it.foodName + " "
                        i++
                    }
                    if (i % 4 == 0) {
                        flourfood += "\n"
                    }

                }
                return flourfood
            }
            CookKind.SoutPorri.kindName -> {
                var soutporri = ""
                var i = 0
                cookbooksList.forEach {
                    if (it.foodCategory == CookKind.SoutPorri.kindName) {
                        soutporri += it.foodName + " "
                        i++
                    }
                    if (i % 4 == 0) {
                        soutporri += "\n"
                    }
                }
                return soutporri
            }
            CookKind.Snackdetail.kindName -> {
                var snackdetail = ""
                var i = 0
                cookbooksList.forEach {
                    if (it.foodCategory == CookKind.Snackdetail.kindName) {
                        snackdetail += it.foodName + " "
                        i++
                    }
                    if (i % 4 == 0) {
                        snackdetail += "\n"
                    }
                }
                return snackdetail
            }
            else -> return ""
        }
    }

    /*
      生成输出文件
     */
    private fun createOutFile(
        date: String
    ) {
        val firstRow = mutableListOf(date, "早餐", "午餐", "晚餐")
        val firstCol = mutableListOf("凉菜", "热菜", "主食", "粥/汤", "明档", "水果")
        //创建文档对象
        val doc = XWPFDocument()
        try {

            //生成一个7行4列的表格
            val nRows = 7
            val nCols = 4
            val table = doc.createTable(nRows, nCols)
            //设置表格风格，如果没有定义风格，则默认“Normal"
            val tblPr = table.ctTbl.tblPr
            val styleStr = tblPr.addNewTblStyle()
            styleStr.`val` = "StyledTable"
            //获取表格中所有行
            val rows = table.rows
            var rowCt = 0
            var colCt = 0
            for (row in rows) {
                //获取表格中行的属性
                val trPr = row.ctRow.addNewTrPr()
                //设置行高，units = twentieth of a point 360=0.25
                val ht = trPr.addNewTrHeight()
                ht.`val` = BigInteger.valueOf(360)

                //获取行中的单元格
                val cells = row.tableCells
                //给每个单元格添加内容
                for (cell in cells) {
                    //获取单元格的属性
                    val tcpr = cell.ctTc.addNewTcPr()
                    //设置垂直方向居中
                    val va = tcpr.addNewVAlign()
                    va.`val` = STVerticalJc.CENTER
                    //生成单元颜色元素
                    val ctshd = tcpr.addNewShd()
                    ctshd.color = "auto"
                    ctshd.`val` = STShd.CLEAR
                    when {
                        rowCt == 0 -> {
                            //表头行,填充色
                            ctshd.fill = "A7BFDE"
                        }
                        rowCt % 2 == 0 -> {
                            //偶数行，填充色
                            ctshd.fill = "D3DFEE"
                        }
                        else -> {
                            ctshd.fill = "EDF2F8"
                        }
                    }

                    //获取单元格的段落列表中的第一段
                    val para = cell.paragraphs[0]
                    //创建一个Run包含内容
                    var rh = para.createRun()
                    //按需要样式设置单元格
                    rh.fontSize = 24
                    rh.fontFamily = "仿体"
                    para.alignment = LEFT
                    when (rowCt) {
                        0 -> {
                            rh = para.createRun()
                            rh.setText(firstRow[colCt])
                            rh.fontFamily = "黑体"
                            rh.fontSize = 26
                            rh.isBold = true
                            para.alignment = CENTER
                        }
                        1 -> {//凉菜
                            when (colCt) {
                                0 -> rh.setText(firstCol[colCt])
                                1 -> rh.setText(getCookBook(CookKind.ColdFood.kindName, breakfast))
                                2 -> rh.setText(getCookBook(CookKind.ColdFood.kindName, lunch))
                                3 -> rh.setText(getCookBook(CookKind.ColdFood.kindName, dinner))
                            }
                        }
                        2 -> {//热菜
                            when (colCt) {
                                0 -> rh.setText(firstCol[colCt])
                                1 -> rh.setText(getCookBook(CookKind.HotFood.kindName, breakfast))
                                2 -> rh.setText(getCookBook(CookKind.HotFood.kindName, lunch))
                                3 -> rh.setText(getCookBook(CookKind.HotFood.kindName, dinner))
                            }
                        }
                        3 -> {//主食
                            when (colCt) {
                                0 -> rh.setText(firstCol[colCt])
                                1 -> rh.setText(getCookBook(CookKind.FlourFood.kindName, breakfast))
                                2 -> rh.setText(getCookBook(CookKind.FlourFood.kindName, lunch))
                                3 -> rh.setText(getCookBook(CookKind.FlourFood.kindName, dinner))
                            }
                        }
                        4 -> {//粥汤
                            when (colCt) {
                                0 -> rh.setText(firstCol[colCt])
                                1 -> rh.setText(getCookBook(CookKind.SoutPorri.kindName, breakfast))
                                2 -> rh.setText(getCookBook(CookKind.SoutPorri.kindName, lunch))
                                3 -> rh.setText(getCookBook(CookKind.SoutPorri.kindName, dinner))
                            }
                        }
                        5 -> { //明档
                            when (colCt) {
                                0 -> rh.setText(firstCol[colCt])
                                1 -> rh.setText(getCookBook(CookKind.SoutPorri.kindName, breakfast))
                                2 -> rh.setText(getCookBook(CookKind.SoutPorri.kindName, lunch))
                                3 -> rh.setText(getCookBook(CookKind.SoutPorri.kindName, dinner))
                            }
                        }
                    }
                    colCt++
                }//单元格
                colCt = 0
                rowCt++
            }//行
            //生成文件
            val outFile = FileOutputStream("/storage/emulated/0/菜单/$date.docx")
            try {
                doc.write(outFile)
            } catch (e: Throwable) {
                defUI.showDialog.value = e.message
            } finally {
                outFile.close()
            }
        } catch (e: Throwable) {
            defUI.showDialog.value = e.message
        } finally {
            doc.close()
        }
    }

    /**
     * 同步数据，主要是因为网络数据可以被不同的人修改，所以在查看菜谱时需要将网络数据与本地数据进行同步。
     * 先读取本地数据，然后读取网络
     */
    suspend fun asyncCookBooks(category: String) {
        var skip = 0
        val query = BmobQuery<CookBooks>()
        query.addWhereEqualTo("foodCategory", category)
        query.count(CookBooks::class.java, object : CountListener() {
            override fun done(count: Int?, e: BmobException?) {
                if (e == null) {
                    launchUI {
                        val f = count!! / 500
                        val where = "{\"foodCategory\":\"$category\"}"
                        val cookbookList = async {
                            val allcookbook: MutableList<CookBooks> = mutableListOf()
                            for (a in 0..f) {
                                val list = async {
                                    repository.getCookBookOfCategory(where, skip).results!!
                                }
                                allcookbook.addAll(
                                    list.await()
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
    suspend fun asyncCrossRefs(category: String) {
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
                                val list = async {
                                    repository.getCookBookGoodsCrossRef(
                                        where = where,
                                        skip = skip
                                    ).results!!
                                }
                                allRef.addAll(
                                    list.await()
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