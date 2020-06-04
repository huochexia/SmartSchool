package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCookbookDetailBinding
import com.goldenstraw.restaurant.databinding.LayoutCoolbookItemBinding
import com.goldenstraw.restaurant.databinding.ViewpageOfCookKindBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.CookBook
import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewDailyMeal
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CookBookViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.util.TimeConverter
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cookbook_detail.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.properties.Delegates

class CookBookDetailFragment : BaseFragment<FragmentCookbookDetailBinding>() {

    lateinit var cookCategory: String
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
            cookCategory = it.getString("cookcategory")
            isSelected = it.getBoolean("isSelected")
            if (isSelected) {
                mealDate = it.getString("mealDate")
                mealTime = it.getString("mealTime")
            }
        }
        toolbar.title = cookCategory
        //如果是用于选择，则显示浮动按钮，用于确定用户的选择
        if (isSelected) {
            fab_choice_cookbook.visibility = View.VISIBLE
            fab_choice_cookbook.setOnClickListener {
                Observable.fromIterable(viewModel.cookbookList)
                    .filter {
                        it.isSelected
                    }
                    .map {
                        NewDailyMeal(mealTime, mealDate, it)
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

        vpAdapter = BaseDataBindingAdapter(
            layoutId = R.layout.viewpage_of_cook_kind,
            dataSource = { viewModel.groupbyKind.keys.toList() },
            dataBinding = { ViewpageOfCookKindBinding.bind(it) },
            callback = { group, binding, _ ->
                //每页数据列表的适配器，每页数据是一个key-value对，列表的数据为value
                var adapter = BaseDataBindingAdapter<CookBook, LayoutCoolbookItemBinding>(
                    layoutId = R.layout.layout_coolbook_item,
                    dataSource = { viewModel.groupbyKind[group]!! },
                    dataBinding = { LayoutCoolbookItemBinding.bind(it) },
                    callback = { cookbook, itembinding, position ->
                        itembinding.cookbook = cookbook
                        //如果是用于选择，则显示复选框。
                        if (isSelected) {
                            itembinding.cbSelectedFood.visibility = View.VISIBLE
                        }
                        itembinding.selected = object : Consumer<CookBook> {
                            override fun accept(t: CookBook) {
                                t.isSelected = !t.isSelected
                            }

                        }
                        itembinding.consumer = object : Consumer<CookBook> {
                            override fun accept(t: CookBook) {
                                AlertDialog.Builder(context!!)
                                    .setMessage("确定要删除吗?")
                                    .setNegativeButton("取消") { dialog, which ->
                                        dialog.dismiss()
                                    }
                                    .setPositiveButton("确定") { dialog, which ->
                                        viewModel.deleteCookBook(cookbook.objectId)
                                        viewModel.groupbyKind[group]!!.remove(cookbook)
                                        viewModel.defUI.refreshEvent.call()
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
            vpAdapter!!.forceUpdate()
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
            tab.text = viewModel.groupbyKind.keys.toList()[position]
        }
        tabLayoutMediator?.attach()
    }

    override fun onResume() {
        super.onResume()
        //启动和增加新菜谱后，需要重新加载内容
        viewModel.getCookBookOfCategory(cookCategory)
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
            R.id.menu_add_cook -> {
                var bundle = Bundle()
                bundle.putString("cookcategory", cookCategory)
                findNavController().navigate(R.id.inputCookBookFragment, bundle)
            }
        }
        return true
    }

}