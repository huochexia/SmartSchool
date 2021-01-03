package com.goldenstraw.restaurant.goodsmanager.http.entities

import cn.bmob.v3.BmobObject
import com.owner.basemodule.room.entities.LocalCookBook
import com.owner.basemodule.room.entities.Material

/**
 * 菜谱这个功能的设计，因为涉及到数组的存储和查询，这部分内容使用API方式比较复杂，而且也不熟悉，
 * 所以采用Bmob提供的Android方式。所有对应数据库的对象都将继承BmobObject
 */
/*
菜谱
 */
data class RemoteCookBook(
    var foodCategory: String,//凉菜，热菜，主食，汤粥，小吃
    var foodKind: String,//素菜，小荤，大荤
    var foodName: String,
    var usedNumber: Int = 0,
    var isSelected: Boolean = false,
    var isStandby: Boolean = false,
    var material: List<Material>
) : BmobObject()

//转换
fun remoteToLocalCookBook(remoteCookBook: RemoteCookBook): LocalCookBook {
    return LocalCookBook(
        objectId = remoteCookBook.objectId,
        foodCategory = remoteCookBook.foodCategory,
        foodKind = remoteCookBook.foodKind,
        foodName = remoteCookBook.foodName,
        usedNumber = remoteCookBook.usedNumber,
        isStandby = remoteCookBook.isStandby
    )
}

fun localToRemoteCookBook(localCookBook: LocalCookBook, materials: List<Material>): RemoteCookBook {
    return RemoteCookBook(
        foodCategory = localCookBook.foodCategory,
        foodName = localCookBook.foodName,
        foodKind = localCookBook.foodKind,
        isStandby = localCookBook.isStandby,
        material = materials
    )
}
/*
  菜谱与商品的新关联关系
 */
data class NewCrossRef(
    var cb_id: String,
    var goods_id: String,
    var foodCategory: String
)

/*
 * 每日菜单
 * 每日菜单是指每天早午晚三餐供应的菜品，它与菜谱有关联关系，因为每道食品都对应着菜谱
 */
data class DailyMeal(
    var mealTime: String,//早、午、餐
    var mealDate: String,//餐日期,比较时加上” 00:00:00"后转换成日期
    var cookBook: LocalCookBook,
    var isOfTeacher: Boolean = false,
    var direct: Int = 0  //0:新石校区，1：西山校区
) : BmobObject()

data class NewDailyMeal(
    var mealTime: String,//早、午、餐
    var mealDate: String,//餐日期,比较时加上” 00:00:00"后转换成日期
    var cookBook: LocalCookBook,
    var isOfTeacher: Boolean = false,
    var direct: Int = 0  //0：新石校区，1：西山校区
)

/*
 *修改每日菜单当中的是否为教职工餐项
 */
data class UpdateIsteacher(
    var isOfTeacher: Boolean
)

/*
 *修改菜谱的状态，常用或备用
 */
data class UpdateIsStandby(
    var isStandby: Boolean
)

/*
  修改菜谱的使用次数
 */
data class UpdateUsedNumber(
    var usedNumber: Int
)


/*
分析菜单数据类
 */
data class AnalyzeMealResult(
    var cold_sucai: Int = 0,
    var cold_xiaohun: Int = 0,
    var cold_dahun: Int = 0,
    var hot_sucai: Int = 0,
    var hot_xiaohun: Int = 0,
    var hot_dahun: Int = 0,
    var flour_mianshi: Int = 0,
    var flour_xianlei: Int = 0,
    var flour_zaliang: Int = 0,
    var soup_tang: Int = 0,
    var soup_zhou: Int = 0,
    var snack_zhu: Int = 0,
    var snack_jianchao: Int = 0,
    var snack_youzha: Int = 0
)

/*
  查询结果计数
 */
data class Count<T>(
    var results: List<T>,
    var count: Int
)