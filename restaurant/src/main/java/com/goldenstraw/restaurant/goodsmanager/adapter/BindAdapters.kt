package com.goldenstraw.restaurant.goodsmanager.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

@BindingAdapter("bind_state_text")
fun setStateText(textView: TextView, state: Int) {
    when (state) {
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
            textView.text = "对帐"
            textView.setTextColor(Color.RED)
        }
    }
}
