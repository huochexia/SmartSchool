package com.goldenstraw.restaurant.goodsmanager.ui.purchase

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityPurchasingManagerBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.di.queryordersactivitymodule
import com.goldenstraw.restaurant.goodsmanager.repositories.goods_order.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.GoodsToOrderMgViewModel
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import kotlinx.android.synthetic.main.activity_purchasing_manager.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.util.*

/**
 * 采购管理：管理商品类别，商品，添加购物车
 */
class PurchasingManagerActivity : BaseActivity<ActivityPurchasingManagerBinding>() {

    lateinit var viewModelGoodsTo: GoodsToOrderMgViewModel


    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(goodsDataSourceModule)
        import(queryordersactivitymodule)
    }
    private val repository by instance<GoodsRepository>()

    override val layoutId: Int
        get() = R.layout.activity_purchasing_manager


    override fun initView() {
        super.initView()
        setSupportActionBar(toolbar)//没有这个显示不了菜单
        viewModelGoodsTo = getViewModel { GoodsToOrderMgViewModel(repository) }

        val categoryFragment =
            CategoryManagerFragment()
        val goodsFragment =
            GoodsManagerFragment()
        val searchFragment =
            GoodsSearchFragment()
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.fragment_category_container, categoryFragment)
        trans.replace(R.id.fragment_goods_container, goodsFragment)
        trans.replace(R.id.search_fragment, searchFragment)
        trans.commit()

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_goods, menu)
        searchGoods(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_shopping_cart -> {
                //打开购物车
                val intent = Intent()
                intent.setClass(this, SubscribeGoodsManagerActivity::class.java)
                startActivityForResult(intent, 1)
            }

            R.id.subscribe_goods -> {

                //从DailyMeal中获取后天菜单中的原材料信息，并加入购物车后，跳转到购物车界面

                showDatePickerDialog(this, 0, Calendar.getInstance())

            }

            else -> super.onOptionsItemSelected(item)

        }
        return true
    }



    /**
     * 日期选择器，确定后复印选择日期的菜单到当前日期
     */
    private fun showDatePickerDialog(
        activity: Activity?,
        themeResId: Int,
        calendar: Calendar
    ) {
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        DatePickerDialog(
            activity!!,
            themeResId,
            { _, year, monthOfYear, dayOfMonth ->
                // 绑定监听器(How the parent is notified that the date is set.)
                // 此处得到选择的时间，可以进行你想要的操作
                val month = if (monthOfYear + 1 < 10) {
                    "0${monthOfYear + 1}"
                } else {
                    "${monthOfYear + 1}"
                }
                val day = if (dayOfMonth < 10) {
                    "0${dayOfMonth}"
                } else {
                    "$dayOfMonth"
                }
                val copyDate = "$year-$month-$day"

                val where = "{\"mealDate\":\"$copyDate\"}"
                viewModelGoodsTo.getFoodOfDailyToShoppingCar(where)
            } // 设置初始日期
            ,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]).show()
    }
    /**
     * 设置查找视图SearchView
     */
    private fun searchGoods(menu: Menu?) {
        //获取SearchView对象
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        val searchEdit = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEdit.setTextColor(Color.BLACK)
        with(searchView) {
            queryHint = "输入商品名称查找"
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //查找框内容变化事件
                    //调用ViewModel当中的查询
                    newText?.let {
                        if (it.isEmpty()) {
                            viewModelGoodsTo.searchGoodsResultList.clear()
                        } else {
                            viewModelGoodsTo.searchGoodsFromName(it.trim())
                        }
                    }
                    return true
                }
            })
            setOnSearchClickListener {
                //开始搜索时的行为，如，搜索结果界面
                list_fragment.visibility = View.GONE
                search_fragment.visibility = View.VISIBLE
            }
            setOnCloseListener {
                //关闭搜索时事件
                list_fragment.visibility = View.VISIBLE
                search_fragment.visibility = View.GONE
                false
            }
        }

    }

}
