package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView

import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSearchCookbookBinding
import com.goldenstraw.restaurant.databinding.LayoutCookbookItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewDailyMeal
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.None
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.Success
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CookBookViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.CookBookWithMaterials
import kotlinx.android.synthetic.main.fragment_search_cookbook.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 模糊查询菜谱
 */
class SearchCookBookFragment : BaseFragment<FragmentSearchCookbookBinding>() {

    private val prefs by instance<PrefsHelper>()
    var mealDate = ""
    var mealTime = ""
    var cookCategory = ""
    override val layoutId: Int
        get() = R.layout.fragment_search_cookbook

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    private val repository by instance<CookBookRepository>()

    var viewModel: CookBookViewModel? = null

    var adapter: BaseDataBindingAdapter<CookBookWithMaterials, LayoutCookbookItemBinding>? = null

    var cookbookList = mutableListOf<CookBookWithMaterials>()
    override fun initView() {
        arguments?.let {
            mealDate = it.getString("mealDate")!!
            mealTime = it.getString("mealTime")!!
            cookCategory = it.getString("cookcategory")!!
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(search_cookbook_toolbar)
        setHasOptionsMenu(true)


        viewModel = activity!!.getViewModel {
            CookBookViewModel(repository)
        }
        //观察LiveData的变化
        viewModel!!.searchCookbookStatusLiveDate.observe(viewLifecycleOwner) { status ->
            cookbookList.clear()
            when (status) {
                null, None -> {
                }
                is Success<*> -> {
                    //刷新列表
                    cookbookList.addAll(status.list as MutableList<CookBookWithMaterials>)
                }
            }
            adapter!!.forceUpdate()
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_cookbook_item,
            dataSource = { cookbookList },
            dataBinding = { LayoutCookbookItemBinding.bind(it) },
            callback = { cookbook, binding, _ ->
                binding.cookbooks = cookbook
                //项目点击事件，返回用户的选择
                binding.onClick = object : Consumer<CookBookWithMaterials> {
                    override fun accept(t: CookBookWithMaterials) {
                        with(viewModel!!) {
                            val newDailyMeal = NewDailyMeal(mealTime, mealDate, t.cookbook,direct = prefs.district)
                            createDailyMeal(newDailyMeal)
                            defUI.refreshEvent.call()
                        }
                        val bundle = Bundle()
                        bundle.putString("mealdate", "$mealDate")
                        findNavController().navigate(R.id.action_searchCookBookFragment_to_dailyMealTimeFragment, bundle)
                    }

                }
            }
        )

        viewModel!!.defUI.showDialog.observe(viewLifecycleOwner) {
            AlertDialog.Builder(context!!)
                .setMessage(it)
                .create()
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        cookbookList.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search_cookbook, menu)
        searchCookBook(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_add_cookbook -> {
                val bundle = Bundle()
                bundle.putString("cookcategory", cookCategory)
                findNavController().navigate(R.id.inputCookBookFragment, bundle)
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
    private fun searchCookBook(menu: Menu?) {
        //获取SearchView对象
        val searchItem = menu?.findItem(R.id.action_search_cookbook)
        val searchView = searchItem?.actionView as SearchView
        searchView.isIconified = false //处于展开状态
        searchView.onActionViewExpanded()
        val searchEdit = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEdit.setTextColor(Color.BLACK)

        with(searchView) {
            queryHint = "输入菜谱名称"
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //查找框内容变化事件
                    //调用ViewModel当中的查询
                    newText?.let {
                        if (it.isEmpty()) {
                            cookbookList.clear()
                            adapter!!.forceUpdate()
                        } else {
                            viewModel!!.searchCookBookWithMaterials(newText.trim(), cookCategory)
                        }
                    }

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