package com.goldenstraw.restaurant.goodsmanager.utils

enum class CookKind(val kindName: String) {
    ColdFood("凉菜"),
    HotFood("热菜"),
    FlourFood("主食"),
    SoutPorri("汤粥"),
    Snackdetail("明档")
}

enum class MealTime(val time: String) {
    Breakfast("早餐"),
    Lunch("午餐"),
    Dinner("晚餐")
}