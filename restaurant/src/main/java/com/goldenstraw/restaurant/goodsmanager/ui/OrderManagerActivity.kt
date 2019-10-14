package com.goldenstraw.restaurant.goodsmanager.ui

import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityOrderManagerBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.OrderMgViewModel
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.util.toast
import kotlinx.android.synthetic.main.activity_order_manager.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class OrderManagerActivity : BaseActivity<ActivityOrderManagerBinding>() {

    lateinit var viewModel: OrderMgViewModel

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(goodsDataSourceModule)
    }
    private val repository: GoodsRepository by instance()

    override val layoutId: Int
        get() = R.layout.activity_order_manager


    override fun initView() {
        super.initView()
        setSupportActionBar(toolbar)//没有这个显示不了菜单
        viewModel = getViewModel { OrderMgViewModel(repository) }
        viewModel.getState().observe(this, Observer { showAddCategoryDialog() })
        val categoryFragment = CategoryManagerFragment()
        val goodsFragment = GoodsManagerFragment()
        val searchFragment = GoodsSearchFragment()
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.fragment_category_container, categoryFragment)
        trans.replace(R.id.fragment_goods_container, goodsFragment)
        trans.replace(R.id.search_fragment, searchFragment)
        trans.commit()
        /*
        暂时在这里实现网络数据与本地数据的同步
         */
        viewModel.syncAllData()

    }

    /**
     * 显示增加类别的对话框
     */
    private fun showAddCategoryDialog() {
        val view = layoutInflater.inflate(R.layout.add_dialog_view, null)
        val editText = view.findViewById<EditText>(R.id.dialog_edit)
        val dialog = AlertDialog.Builder(this)
            .setIcon(R.mipmap.add_icon)
            .setTitle("增加商品类别")
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                var content = editText.text.toString().trim()
                if (content.isNullOrEmpty()) {
                    toast { "请填写必须内容！！" }
                } else {
                    viewModel.addCategoryToRepository(content)
                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }

    /**
     * 显示增加商品的对话框
     */
    private fun showAddGoodsDialog(category: GoodsCategory) {
        val view = layoutInflater.inflate(R.layout.add_goods_dialog_view, null)
        val goodsName = view.findViewById<EditText>(R.id.et_goods_name)
        val unitOfMeasure = view.findViewById<EditText>(R.id.et_unit_of_measure)
        val dialog = AlertDialog.Builder(this)
            .setIcon(R.mipmap.add_icon)
            .setTitle("增加商品----" + category.categoryName)
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                val name = goodsName.text.toString().trim()
                val unit = unitOfMeasure.text.toString().trim()
                if (name.isNullOrEmpty() || unit.isNullOrEmpty()) {
                    toast { "请填写必须内容！！" }
                } else {
                    val goods = NewGoods(
                        goodsName = name,
                        unitOfMeasurement = unit,
                        categoryCode = category.objectId
                    )
                    viewModel.addGoodsToRepository(goods)
                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_goods, menu)
        searchGoods(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_shopping_cart -> {
                toast { "待开发。。。。。" }
            }
            R.id.action_add_goods_item -> {
                if (viewModel.selected.value == null)
                    toast { "请先确定商品所属类别" }
                else
                    showAddGoodsDialog(viewModel.selected.value!!)
            }
            else -> super.onOptionsItemSelected(item)

        }
        return true
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
                            viewModel.searchGoodsResultList.clear()
                        } else {
                            viewModel.searchGoodsFromName(it.trim())
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
