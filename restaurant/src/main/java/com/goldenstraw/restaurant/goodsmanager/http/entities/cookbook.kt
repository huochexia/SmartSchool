package com.goldenstraw.restaurant.goodsmanager.http.entities

import com.owner.basemodule.room.entities.Goods

/**
 * 菜谱
 */
data class CookBook(
    val objectId: String,
    var foodCategory: String,//凉菜，热菜，主食，汤粥，小吃
    var foodKind: String,//素菜，小荤，大荤
    var foodName: String,
    var firstMaterial: Goods,//必须有一个主料
    var secondMaterial: Goods = Goods("", "", "", 0.0f, ""),
    var thirdMaterial: Goods = Goods("", "", "", 0.0f, ""),
    var fourthMaterial: Goods = Goods("", "", "", 0.0f, ""),
    var isSelected: Boolean = false
)

/**
 * 将网络获取的数据，拷贝一份，主要用于网络修改API
 */
fun CookBook.copy(): NewCookBook {
    return NewCookBook(
        this.foodCategory,
        this.foodKind,
        this.foodName,
        this.firstMaterial,
        this.secondMaterial,
        this.thirdMaterial,
        this.fourthMaterial,
        this.isSelected
    )
}

/**
 * 新菜谱,因为网络数据库为数据自动添加objectId，所以生成的对象不能有objectId属性
 */
data class NewCookBook(
    var foodCategory: String,//凉菜，热菜，主食，汤粥，小吃
    var foodKind: String,//素菜，小荤，大荤
    var foodName: String,
    var firstMaterial: Goods,//必须有一个主料
    var secondMaterial: Goods = Goods("", "", "", 0.0f, ""),
    var thirdMaterial: Goods = Goods("", "", "", 0.0f, ""),
    var fourthMaterial: Goods = Goods("", "", "", 0.0f, ""),
    var isSelected: Boolean = false
)


/**
 * 每日餐时
 */
data class DailyMeal(
    var objectId: String = "",
    var mealTime: String,//早、午、餐
    var mealDate: String,//餐日期
    var foodCategory: String,//凉，热，主食，汤粥，小吃，水果
    var foodName: String,
    var isOfTeacher: Boolean
)

data class NewDailyMeal(
    var mealTime: String,//早、午、餐
    var mealDate: String,//餐日期
    var foodCategory: String,//凉，热，主食，汤粥，小吃，水果
    var foodName: String,
    var isOfTeacher: Boolean
)

/**
 * 扩展，将数据库中得到的每日菜单，拷贝成一个新的，主要用于网络修改Api
 */
fun DailyMeal.copy(): NewDailyMeal {
    return NewDailyMeal(
        this.mealTime,
        this.mealDate,
        this.foodCategory,
        this.foodName,
        this.isOfTeacher
    )
}