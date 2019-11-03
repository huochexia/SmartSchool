package com.goldenstraw.restaurant.goodsmanager.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.owner.basemodule.room.entities.User

class SupplierSpinnerAdapter(
    private val mContext: Context,
    private val list: MutableList<User>
) :
    BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val ll = LinearLayout(mContext)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.gravity = Gravity.CENTER_HORIZONTAL
        val tv = TextView(mContext)
        tv.text = list[position].username
        tv.textSize = 20f
        tv.setTextColor(Color.BLACK)
        ll.addView(tv)
        return ll
    }
}