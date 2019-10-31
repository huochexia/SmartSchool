package com.goldenstraw.restaurant.goodsmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import arrow.instance
import com.alibaba.android.arouter.facade.annotation.Route
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityMain2Binding
import com.goldenstraw.restaurant.goodsmanager.ui.check.CheckQuantityActivity
import com.goldenstraw.restaurant.goodsmanager.ui.goods.OrderManagerActivity
import com.goldenstraw.restaurant.goodsmanager.ui.verify.VerifyAndPlaceOrderActivity
import com.goldenstraw.restaurant.goodsmanager.ui.query.QueryOrdersActivity
import com.goldenstraw.restaurant.goodsmanager.ui.record.RecordOrdersActivity
import com.goldenstraw.restaurant.goodsmanager.ui.supplier.SupplierApplyActivity
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.owner.basemodule.base.view.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_main2.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

@Route(path = "/restaurant/MainActivity")
class Main2Activity : BaseActivity<ActivityMain2Binding>() {
    override val layoutId: Int
        get() = R.layout.activity_main2
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        bind<PrefsHelper>() with provider {
            PrefsHelper(instance())
        }
    }
    private val prefs: PrefsHelper by instance()
    override fun initView() {
        hideAllFunction()
        when (prefs.role) {
            "供应商" -> {
                supplier.visibility = View.VISIBLE
            }
            "管理员" -> {
                when (prefs.rights) {
                    "记帐" -> {
                        record.visibility = View.VISIBLE
                    }
                    "主管" -> {
                        order.visibility = View.VISIBLE
                        send.visibility = View.VISIBLE
                        query.visibility = View.VISIBLE
                        supplier.visibility = View.VISIBLE
                        check.visibility = View.VISIBLE
                        record.visibility = View.VISIBLE
                    }
                    "审核" -> {
                        send.visibility = View.VISIBLE
                    }
                    "库管员" -> {
                        order.visibility = View.VISIBLE
                        check.visibility = View.VISIBLE
                    }
                }
            }
        }
        initEvent()
    }

    private fun initEvent() {
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

    fun hideAllFunction() {
        order.visibility = View.GONE
        send.visibility = View.GONE
        query.visibility = View.GONE
        supplier.visibility = View.GONE
        check.visibility = View.GONE
        record.visibility = View.GONE
    }
}
