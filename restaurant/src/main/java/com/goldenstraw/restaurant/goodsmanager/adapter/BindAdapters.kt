package com.goldenstraw.restaurant.goodsmanager.adapter

import android.graphics.Color
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem

@BindingAdapter("bind_state_text")
fun setStateText(textView: TextView, state: Int) {
    when (state) {
        -1 -> {
            textView.text = "退货"
            textView.setTextColor(Color.DKGRAY)
        }
        0 -> {
            textView.text = "订货"
            textView.setTextColor(Color.BLUE)
        }
        1 -> {
            textView.text = "送货"
            textView.setTextColor(Color.GREEN)
        }
        2 -> {
            textView.text = "验收"
            textView.setTextColor(Color.CYAN)
        }
        3 -> {
            textView.text = "记帐"
            textView.setTextColor(Color.RED)
        }
    }
}

@BindingAdapter("bind_quantity_text")
fun setQuantityText(textView: TextView, order: OrderItem) {
    if (order.state == 1 || order.state == 0)
        textView.text = order.quantity.toString()
    else
        textView.text = order.checkQuantity.toString()
}
