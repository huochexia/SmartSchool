package com.goldenstraw.restaurant.goodsmanager.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop

class KindSpinnerAdapter(private val mContext: Context, val list: MutableList<String>) :
    BaseAdapter() {
    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Any = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val ll = LinearLayout(mContext)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.gravity = Gravity.CENTER_HORIZONTAL

        val tv = TextView(mContext)
        tv.text = list[position]
        tv.textSize = 16f
        ll.addView(tv)
        return ll
    }
}