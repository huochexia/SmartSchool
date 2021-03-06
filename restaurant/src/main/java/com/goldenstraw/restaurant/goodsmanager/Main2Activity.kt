package com.goldenstraw.restaurant.goodsmanager

import android.content.Intent
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityMain2Binding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.repositories.goods_order.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.ui.adjustprice.AdjustPriceOfGoodsActivity
import com.goldenstraw.restaurant.goodsmanager.ui.check.CheckQuantityActivity
import com.goldenstraw.restaurant.goodsmanager.ui.confirm.ConfirmQuantityActivity
import com.goldenstraw.restaurant.goodsmanager.ui.cookbook.CookBookMainActivity
import com.goldenstraw.restaurant.goodsmanager.ui.purchase.PurchasingManagerActivity
import com.goldenstraw.restaurant.goodsmanager.ui.query.QueryOrdersActivity
import com.goldenstraw.restaurant.goodsmanager.ui.record.RecordOrdersActivity
import com.goldenstraw.restaurant.goodsmanager.ui.supplier.SupplierApplyActivity
import com.goldenstraw.restaurant.goodsmanager.ui.verify.VerifyAndSendOrderActivity
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.GoodsToOrderMgViewModel
import com.owner.basemodule.arouter.RouterPath
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.util.toast
import kotlinx.android.synthetic.main.activity_main2.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

@Route(path = "/restaurant/main")
class Main2Activity : BaseActivity<ActivityMain2Binding>() {
    override val layoutId: Int
        get() = R.layout.activity_main2
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(goodsDataSourceModule)
        bind<PrefsHelper>() with provider {
            PrefsHelper(instance())
        }
    }
    private val repository by instance<GoodsRepository>()
    lateinit var viewModelGoodsTo: GoodsToOrderMgViewModel

    private val prefs by instance<PrefsHelper>()
    override fun initView() {
        setSupportActionBar(main_toolbar)
        hideAllFunction()
        when (prefs.role) {
            "供应商" -> {
                supplier.visibility = View.VISIBLE
                val intent2 = Intent(this, SupplierApplyActivity::class.java)
                startActivity(intent2)
            }
            "管理员" -> {
                check.visibility = View.VISIBLE
                confirm.visibility = View.VISIBLE
                order.visibility = View.VISIBLE
                send.visibility = View.VISIBLE
                query.visibility = View.VISIBLE
                manager.visibility = View.VISIBLE
                adjustment.visibility = View.VISIBLE
                cookbook.visibility = View.VISIBLE
            }
            "库管员" -> {
                order.visibility = View.VISIBLE
                check.visibility = View.VISIBLE
                record.visibility = View.VISIBLE
                cookbook.visibility = View.VISIBLE
            }
            "复核员" -> {
                confirm.visibility = View.VISIBLE
                cookbook.visibility = View.VISIBLE
            }
            "厨师","热菜主管","凉菜主管","主食主管" -> {
                order.visibility = View.VISIBLE
                cookbook.visibility = View.VISIBLE
            }

        }

        initEvent()
        //将网络数据同步本地
        viewModelGoodsTo = getViewModel {
            GoodsToOrderMgViewModel(repository)
        }
        viewModelGoodsTo.syncAllData()
    }

    private fun initEvent() {
        order.setOnClickListener {
            val intent = Intent(this, PurchasingManagerActivity::class.java)
            startActivity(intent)
        }
        send.setOnClickListener {
            val intent1 = Intent(this, VerifyAndSendOrderActivity::class.java)
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
        confirm.setOnClickListener {
            val intent3 = Intent(this, ConfirmQuantityActivity::class.java)
            startActivity(intent3)
        }
        manager.setOnClickListener {
            ARouter.getInstance().build(RouterPath.UserCenter.PATH_USERMANAGER).navigation()

        }
        adjustment.setOnClickListener{
            val intentAdjust = Intent(this, AdjustPriceOfGoodsActivity::class.java)
            startActivity(intentAdjust)
        }
        cookbook.setOnClickListener {
            val intentCook = Intent(this, CookBookMainActivity::class.java)
            startActivity(intentCook)
        }

    }

    private fun hideAllFunction() {
        order.visibility = View.GONE
        send.visibility = View.GONE
        query.visibility = View.GONE
        supplier.visibility = View.GONE
        check.visibility = View.GONE
        record.visibility = View.GONE
        confirm.visibility = View.GONE
        manager.visibility = View.GONE
        adjustment.visibility=View.GONE
        cookbook.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout_user, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {

            R.id.logout_user -> {

                prefs.autoLogin = false
            }

        }
        finish()
        return true
    }

    /**
     * 实现再按一次返回键退出程序
     */
    private var exitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 3000) {
                toast { "再按一次退出程序！" }
                exitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
