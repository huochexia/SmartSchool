package com.goldenstraw.restaurant.goodsmanager.adapter

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.http.entities.SumByGroup
import com.owner.basemodule.room.entities.CookBookWithMaterials
import java.text.DecimalFormat

@BindingAdapter("bind_state_text")
fun setStateText(textView: TextView, state: Int) {
    when (state) {
        -1 -> {
            textView.text = "退"
            textView.setTextColor(Color.RED)
        }
        0 -> {
            textView.text = "订"
            textView.setTextColor(Color.CYAN)
        }
        1 -> {
            textView.text = "送"
            textView.setTextColor(Color.GREEN)
        }
        2 -> {
            textView.text = "验"
            textView.setTextColor(Color.BLUE)
        }
        3 -> {
            textView.text = "定"
            textView.setTextColor(Color.YELLOW)
        }
        4 -> {
            textView.text = "记"
            textView.setTextColor(Color.BLACK)
        }
    }
}

@BindingAdapter("bind_cookbook_color")
fun setCookBookColor(textView: TextView, isStandby: Boolean) {
    if (isStandby) {
        textView.setTextColor(Color.BLUE)
    } else {
        textView.setTextColor(Color.BLACK)
    }
}

@BindingAdapter("bind_dailymeal_text")
fun setDailyMealText(textView: TextView, dailyMeal: DailyMeal) {
    textView.text = dailyMeal.cookBook.foodName
    when (dailyMeal.cookBook.foodKind) {
        "小荤菜", "煮", "汤", "馅类" -> textView.setTextColor(Color.BLUE)
        "大荤菜", "杂粮", "煎炒" -> textView.setTextColor(Color.RED)
        else -> textView.setTextColor(Color.BLACK)
    }
}

@BindingAdapter("bind_food_time_image")
fun setFoodTimeImage(imageView: ImageView, mealTime: String) {
    when (mealTime) {
        "午餐" -> imageView.load(R.drawable.ic_sun_24)
        "晚餐" -> imageView.load(R.drawable.ic_bedtime_24)
    }
}

@BindingAdapter("bind_quantity_text")
fun setQuantityText(textView: TextView, order: OrderItem) {
    if (order.state == 1 || order.state == 0 || order.state == -1)
        textView.text = order.quantity.toString()
    else
        textView.text = order.checkQuantity.toString()
}

@BindingAdapter("bind_difference_text")
fun setDifferenceText(textView: TextView, order: OrderItem) {
    val format = DecimalFormat("0")
    val differ = (order.againCheckQuantity - order.checkQuantity) * order.unitPrice
    textView.text = format.format(differ)
}

@BindingAdapter("bind_average_text")
fun setAverageText(textView: TextView, sumByGroup: SumByGroup) {
    val format = DecimalFormat(".00")
    val average = (sumByGroup._sumTotal) / sumByGroup._sumCheckQuantity
    textView.text = format.format(average)
}

@BindingAdapter("bind_text_color")
fun setTextColor(textView: TextView, distinct: Int) {
    when (distinct) {
        0 -> {
            textView.setTextColor(Color.BLUE)
        }
        1 -> {
            textView.setTextColor(Color.GREEN)
        }
    }
}

/**
 * 用于修饰菜谱中主料列表的显示。
 */
@BindingAdapter("bind_list_content")
fun setListContent(textView: TextView, cookbooks: CookBookWithMaterials) {
    var text = ""
    cookbooks.materials.forEach {
        text = "$text${it.goodsName},"
//        text = "$text${it.goodsName}[${it.ration}],"

    }
    textView.text = text
}