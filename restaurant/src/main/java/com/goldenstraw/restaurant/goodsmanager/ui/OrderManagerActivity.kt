package com.goldenstraw.restaurant.goodsmanager.ui

import android.graphics.Color
import android.view.Menu
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityOrderManagerBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.OrderMgViewModel
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
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
//        val host: NavHostFragment = supportFragmentManager
//            .findFragmentById(R.id.order_Manager_Fragment) as NavHostFragment? ?: return
        viewModel = getViewModel { OrderMgViewModel(repository) }
        viewModel.getState().observe(this, Observer { showAddCategoryDialog() })

        val categoryFragment = CategoryManagerFragment()
        val goodsFragment = GoodsManagerFragment()
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.fragment_category_container, categoryFragment)
        trans.replace(R.id.fragment_goods_container, goodsFragment)
        trans.commit()

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
                var content = editText.text.toString()
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_goods, menu)
        searchGoods(menu)
        return super.onCreateOptionsMenu(menu)
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
                    return false
                }
            })
            setOnSearchClickListener {
                //开始搜索时的行为，如，搜索结果界面
            }
            setOnCloseListener {
                //关闭搜索时事件
                false
            }
        }

    }

}
