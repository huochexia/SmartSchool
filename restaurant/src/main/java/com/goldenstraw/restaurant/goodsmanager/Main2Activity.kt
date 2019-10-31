package com.goldenstraw.restaurant.goodsmanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.goodsmanager.ui.check.CheckQuantityActivity
import com.goldenstraw.restaurant.goodsmanager.ui.goods.OrderManagerActivity
import com.goldenstraw.restaurant.goodsmanager.ui.verify.VerifyAndPlaceOrderActivity
import com.goldenstraw.restaurant.goodsmanager.ui.query.QueryOrdersActivity
import com.goldenstraw.restaurant.goodsmanager.ui.record.RecordOrdersActivity
import com.goldenstraw.restaurant.goodsmanager.ui.supplier.SupplierApplyActivity
import kotlinx.android.synthetic.main.activity_main2.*
@Route(path = "/restaurant/MainActivity")
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
        check.setOnClickListener {
            val intent2 = Intent(this, CheckQuantityActivity::class.java)
            startActivity(intent2)
        }
        record.setOnClickListener {
            val intent2 = Intent(this, RecordOrdersActivity::class.java)
            startActivity(intent2)
        }
    }
}
