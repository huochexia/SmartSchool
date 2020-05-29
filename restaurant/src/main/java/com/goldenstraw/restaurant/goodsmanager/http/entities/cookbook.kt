package com.goldenstraw.restaurant.goodsmanager.http.entities

import cn.bmob.v3.BmobObject
import cn.bmob.v3.exception.BmobException
import com.owner.basemodule.room.entities.Goods

/**
 * 菜谱这个功能的设计，因为涉及到数组的存储和查询，这部分内容使用API方式比较复杂，而且也不熟悉，
 * 所以采用Bmob提供的Android方式。所有对应数据库的对象都将继承BmobObject
 */
/*
菜谱
 */
data class CookBook(
    var foodCategory: String,//凉菜，热菜，主食，汤粥，小吃
    var foodKind: String,//素菜，小荤，大荤
    var foodName: String,
    var material: List<Goods>,
    var isSelected: Boolean = false
) : BmobObject()


/*
 * 每日菜单
 * 每日菜单是指每天早午晚三餐供应的菜品，它与菜谱有关联关系，因为每道食品都对应着菜谱
 */
data class DailyMeal(
    var mealTime: String,//早、午、餐
    var mealDate: String,//餐日期,比较时加上” 00:00:00"后转换成日期
    var cookBook: CookBook,
    var isOfTeacher: Boolean = false
) : BmobObject()

data class NewDailyMeal(
    var mealTime: String,//早、午、餐
    var mealDate: String,//餐日期,比较时加上” 00:00:00"后转换成日期
    var cookBook: CookBook,
    var isOfTeacher: Boolean = false
)

/*
 *修改每日菜单当中的是否为教职工餐项
 */
data class UpdateIsteacher(
    var isOfTeacher: Boolean
)
/*
返回结果:使用BmobApi执行网络数据管理的返回对象。
 */
class Results<T>(
    var success: T?,
    var failure: BmobException?
)