package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCookbookDetailBinding
import com.goldenstraw.restaurant.databinding.LayoutCookbookItemBinding
import com.goldenstraw.restaurant.databinding.ViewpageOfCookKindBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewDailyMeal
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CookBookViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.CookBookWithMaterials
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cookbook_detail.*
import kotlinx.coroutines.launch
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import kotlin.properties.Delegates

class CookBookDetailFragment : BaseFragment<FragmentCookbookDetailBinding>() {


    private val prefs by instance<PrefsHelper>()
    lateinit var cookCategory: String
    var isStandby = false
    var isSelected by Delegates.notNull<Boolean>() //判断是用于选择还是查看
    var mealDate = ""
    var mealTime = ""
    var tabLayoutMediator: TabLayoutMediator? = null

    override val layoutId: Int
        get() = R.layout.fragment_cookbook_detail
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    private val respository by instance<CookBookRepository>()
    lateinit var viewModel: CookBookViewModel

    //viewPager2适配器
    var vpAdapter: BaseDataBindingAdapter<String, ViewpageOfCookKindBinding>? = null

    @SuppressLint("AutoDispose")
    override fun initView() {
        super.initView()
        arguments?.let {
            cookCategory = it.getString("cookcategory")!!
            isStandby = it.getBoolean("isStandby")
            isSelected = it.getBoolean("isSelected")
            if (isSelected) {
                mealDate = it.getString("mealDate")!!
                mealTime = it.getString("mealTime")!!
            }
        }
        toolbar.title = cookCategory
        //如果是用于选择，则显示浮动按钮，用于确定用户的选择
        if (isSelected) {
            fab_choice_cookbook.visibility = View.VISIBLE
            fab_choice_cookbook.setOnClickListener {
                Observable.fromIterable(viewModel.cookbookList)
                    .filter {
                        it.cookbook.isSelected
                    }
                    .map {
                        NewDailyMeal(mealTime, mealDate, it.cookbook, direct = prefs.district)
                    }.subscribeOn(Schedulers.io())
                    .subscribe({
                        viewModel.createDailyMeal(it)
                    }, {}, {
                        findNavController().popBackStack()
                    })
            }
        }

    }

    @SuppressLint("AutoDispose")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        viewModel = activity!!.getViewModel {

            CookBookViewModel(respository)

        }

        //根据菜谱的分类动态形成标签
        vpAdapter = BaseDataBindingAdapter(
            layoutId = R.layout.viewpage_of_cook_kind,
            dataSource = { viewModel.groupbyKind.keys.toList() },
            dataBinding = { ViewpageOfCookKindBinding.bind(it) },
            callback = { group, binding, _ ->
                //每页数据列表的适配器，每页数据是一个key-value对，列表的数据为value
                val adapter =
                    BaseDataBindingAdapter<CookBookWithMaterials, LayoutCookbookItemBinding>(
                        layoutId = R.layout.layout_cookbook_item,
                        dataSource = { viewModel.groupbyKind[group]!! },
                        dataBinding = { LayoutCookbookItemBinding.bind(it) },
                        callback = { cookbooks, itembinding, _ ->

                            itembinding.cookbooks = cookbooks
                            //如果是用于选择，则显示复选框。
                            if (isSelected) {
                                itembinding.cbSelectedFood.visibility = View.VISIBLE
                            }
                            itembinding.selected = object : Consumer<CookBookWithMaterials> {
                                override fun accept(t: CookBookWithMaterials) {
                                    t.cookbook.isSelected = !t.cookbook.isSelected
                                }

                            }
                            itembinding.onClick = object : Consumer<CookBookWithMaterials> {
                                override fun accept(t: CookBookWithMaterials) {
                                    AlertDialog.Builder(context!!)
                                        .setMessage("要修改${t.cookbook.foodName}吗?")
                                        .setNegativeButton("取消") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        .setPositiveButton("确实") { dialog, _ ->
                                            viewModel.materialList.clear()
                                            viewModel.materialList.addAll(t.materials)
                                            val bundle = Bundle()
                                            bundle.putSerializable("food", t.cookbook)
                                            bundle.putBoolean("isUpdate", true)
                                            bundle.putString("cookcategory", cookCategory)
                                            findNavController().navigate(
                                                R.id.inputCookBookFragment,
                                                bundle
                                            )
                                            dialog.dismiss()
                                        }
                                        .create().show()
                                }

                            }
                            itembinding.consumer = object : Consumer<CookBookWithMaterials> {
                                override fun accept(t: CookBookWithMaterials) {
                                    AlertDialog.Builder(context!!)
                                        .setMessage("确定要删除吗?")
                                        .setNegativeButton("取消") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        .setPositiveButton("确定") { dialog, _ ->
                                            viewModel.deleteCookBook(t)
                                            viewModel.groupbyKind[group]!!.remove(t)
                                            vpAdapter!!.forceUpdate()
                                            dialog.dismiss()
                                        }
                                        .create()
                                        .show()
                                }
                            }
                        }
                    )
                binding.rlvCookbook.adapter = adapter

            }
        )

        viewModel.defUI.refreshEvent.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                viewModel.getCookBookWithMaterialsOfCategory(cookCategory, isStandby)
                vpAdapter!!.forceUpdate()
            }
        }
        viewModel.defUI.showDialog.observe(viewLifecycleOwner) {
            AlertDialog.Builder(context!!)
                .setMessage(it)
                .create()
                .show()
        }
        vp_cook_kind.adapter = vpAdapter
        /*
          ViewPager与TabLayout绑定
         */
        tabLayoutMediator = TabLayoutMediator(tab_cook_kind, vp_cook_kind) { tab, position ->
            with(viewModel.groupbyKind.keys) {
                val key = this.toList()[position]
                val q = viewModel.groupbyKind[key]?.size
                tab.text = "$key($q)"
            }
        }
        tabLayoutMediator?.attach()

    }

    override fun onResume() {
        super.onResume()
        //启动和增加新菜谱后，需要重新加载内容
        launch {
            viewModel.getCookBookWithMaterialsOfCategory(cookCategory, isStandby)
            vpAdapter?.forceUpdate()
        }
    }

    override fun onDestroy() {
        tabLayoutMediator?.detach()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_cookbook, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_cookbook->{
                val bundle = Bundle()
                bundle.putBoolean("isSelected", true)
                bundle.putString("mealDate", "$mealDate")
                bundle.putString("mealTime", "$mealTime")
                bundle.putString("cookcategory", cookCategory)
                findNavController().navigate(R.id.action_cookBookDetailFragment_to_searchCookBookFragment,bundle)
            }
            R.id.menu_add_cook -> {
                viewModel.materialList.clear()
                val bundle = Bundle()
                bundle.putString("cookcategory", cookCategory)
                bundle.putBoolean("isUpdate", false)
                findNavController().navigate(R.id.inputCookBookFragment, bundle)
            }
            R.id.menu_use_cook -> {
                isStandby = false
                launch {
                    viewModel.getCookBookWithMaterialsOfCategory(cookCategory, isStandby)
                    vpAdapter?.forceUpdate()
                }

            }
            R.id.menu_standby_cook -> {
                isStandby = true
                launch {
                    viewModel.getCookBookWithMaterialsOfCategory(cookCategory, isStandby)
                    vpAdapter?.forceUpdate()
                }
            }
        }
        return true
    }

}