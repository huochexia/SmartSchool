package com.goldenstraw.restaurant.goodsmanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.goodsmanager.ui.goods_order.OrderManagerActivity
import com.goldenstraw.restaurant.goodsmanager.ui.place_order.VerifyAndPlaceOrderActivity
import com.goldenstraw.restaurant.goodsmanager.ui.query_orders.QueryOrdersActivity
import com.goldenstraw.restaurant.goodsmanager.ui.supplier.SupplierApplyActivity
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        order.setOnClickListener {
            val intent = Intent(this, OrderManagerActivity::class.java)
            startActivity(intent)
        }
        send.setOnClickListener {
            val intent1 = Intent(this, VerifyAndPlaceOrderActivity::class.java)
            startActivity(intent1)
        }
        query.setOnClickListener {
            val intent2 = Intent(this, QueryOrdersActivity::class.java)
            startActivity(intent2)
        }
        supplier.setOnClickListener {
            val intent2 = Intent(this, SupplierApplyActivity::class.java)
            startActivity(intent2)
        }
    }
}