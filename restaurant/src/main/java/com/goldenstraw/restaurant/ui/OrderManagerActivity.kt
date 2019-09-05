package com.goldenstraw.restaurant.ui

import android.graphics.Color
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.NavHostFragment
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityOrderManagerBinding
import com.owner.basemodule.base.view.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_order_manager.*
import org.kodein.di.Copy
import org.kodein.di.Kodein

class OrderManagerActivity : BaseActivity<ActivityOrderManagerBinding>() {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    override val layoutId: Int
        get() = R.layout.activity_order_manager

    override fun initView() {
        super.initView()
        setSupportActionBar(toolbar)//没有这个显示不了菜单
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.order_Manager_Fragment) as NavHostFragment? ?: return
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_goods, menu)

        //获取SearchView对象
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView


        val searchEdit = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEdit.setTextColor(Color.BLACK)

        with(searchView) {
            queryHint="输入商品名称查找"
            setOnQueryTextListener(object:SearchView.OnQueryTextListener{
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

        return super.onCreateOptionsMenu(menu)
    }
}
