package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSearchMaterialBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_cookbook_detail.*
import org.kodein.di.Copy
import org.kodein.di.Kodein

/**
 * 通过模糊查询，从Goods中查找菜谱所需材料。
 */
class SearchMaterialFragment : BaseFragment<FragmentSearchMaterialBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_search_material

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search_material, menu)
        searchMaterial(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_search_material -> {
            }
            R.id.action_add_material -> {
            }
        }
        return true
    }
    /**
     *
     */
    /**
     * 设置查找视图SearchView
     */
    private fun searchMaterial(menu: Menu?) {
        //获取SearchView对象
        val searchItem = menu?.findItem(R.id.action_search_material)
        val searchView = searchItem?.actionView as SearchView
        searchView.isIconified = false //处于展开状态
        searchView.onActionViewExpanded()
        val searchEdit = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEdit.setTextColor(Color.BLACK)

        with(searchView) {
            queryHint = "输入材料名称"
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //查找框内容变化事件
                    //调用ViewModel当中的查询

                    return true
                }
            })
            setOnSearchClickListener {

            }
            setOnCloseListener {
                //关闭搜索时事件

                false
            }
        }

    }
}