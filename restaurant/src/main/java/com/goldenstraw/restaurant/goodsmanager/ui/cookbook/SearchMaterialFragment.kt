package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView

import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSearchMaterialBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.None
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository.SearchedStatus.Success
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CookBookViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.goodsToMaterial
import kotlinx.android.synthetic.main.fragment_cookbook_detail.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 通过模糊查询，从Goods中查找菜谱所需材料。
 */
class SearchMaterialFragment : BaseFragment<FragmentSearchMaterialBinding>() {


    override val layoutId: Int
        get() = R.layout.fragment_search_material

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    private val repository by instance<CookBookRepository>()

    var viewModel: CookBookViewModel? = null

    var adapter: BaseDataBindingAdapter<Goods, LayoutGoodsItemBinding>? = null

    var goodsList = mutableListOf<Goods>()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        viewModel = activity!!.getViewModel {
            CookBookViewModel(repository)
        }

        //观察LiveData的变化
        viewModel!!.searchedGoodsStatusLiveData.observe(viewLifecycleOwner) { status ->
//            goodsList.clear()
            when (status) {
                null, None -> {
                }
                is Success<*> -> {
                    //刷新列表
                    goodsList.addAll(status.list as MutableList<Goods>)
                }
            }
            adapter!!.forceUpdate()
        }

        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_goods_item,
            dataSource = { goodsList },
            dataBinding = { LayoutGoodsItemBinding.bind(it) },
            callback = { goods, binding, position ->
                binding.goods = goods
                binding.addSub.visibility = View.INVISIBLE
                binding.cbGoods.visibility = View.INVISIBLE
                //项目点击事件，返回用户的选择
                binding.clickEvent = object : Consumer<Goods> {
                    override fun accept(t: Goods) {
                        with(viewModel!!) {
                            //将商品转换为原材料
                            materialList.add(goodsToMaterial(goods))
                            defUI.refreshEvent.call()
                        }
                        findNavController().popBackStack()
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
        goodsList.clear()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search_material, menu)
        searchMaterial(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

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
                    newText?.let {
                        if (it.isEmpty()) {
                            goodsList.clear()
                            adapter!!.forceUpdate()
                        } else {
                            viewModel!!.searchMaterial(newText.trim())
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