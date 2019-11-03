package com.goldenstraw.restaurant.goodsmanager.adapter

import android.graphics.Color
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem

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
            textView.text = "记"
            textView.setTextColor(Color.BLACK)
        }
    }
}

@BindingAdapter("bind_quantity_text")
fun setQuantityText(textView: TextView, order: OrderItem) {
    if (order.state == 1 || order.state == 0 || order.state == -1)
        textView.text = order.quantity.toString()
    else
        textView.text = order.checkQuantity.toString()
}
