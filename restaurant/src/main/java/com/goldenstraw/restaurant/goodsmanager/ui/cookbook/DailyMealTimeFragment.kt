package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.R.color
import com.goldenstraw.restaurant.databinding.FragmentDailyMealtimeBinding
import com.goldenstraw.restaurant.databinding.LayoutMealItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.DailyMeal
import com.goldenstraw.restaurant.goodsmanager.http.entities.UpdateIsteacher
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.utils.CookKind
import com.goldenstraw.restaurant.goodsmanager.utils.CookKind.*
import com.goldenstraw.restaurant.goodsmanager.utils.MealTime
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CookBookViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import kotlinx.android.synthetic.main.fragment_daily_mealtime.*
import kotlinx.android.synthetic.main.viewpage_of_daily_meal.*
import kotlinx.coroutines.launch
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.util.*


/**
 * 订制每日菜单
 */
class DailyMealTimeFragment : BaseFragment<FragmentDailyMealtimeBinding>() {

    private val prefs by instance<PrefsHelper>()
    var isShowAdd = false //用于对除厨师之外的管理员隐藏每日菜单的添加功能

    private val respository by instance<CookBookRepository>()

    lateinit var viewModel: CookBookViewModel

    /*
    针对不同种类菜品，分别定义相应的适配器
     */
    var coldAdapter: BaseDataBindingAdapter<DailyMeal, LayoutMealItemBinding>? = null //凉菜
    var hotAdapter: BaseDataBindingAdapter<DailyMeal, LayoutMealItemBinding>? = null  //热菜
    var flourAdapter: BaseDataBindingAdapter<DailyMeal, LayoutMealItemBinding>? = null //主食
    var soupAdapter: BaseDataBindingAdapter<DailyMeal, LayoutMealItemBinding>? = null  //汤粥
    var snackAdapter: BaseDataBindingAdapter<DailyMeal, LayoutMealItemBinding>? = null //明档

    /*
    查询条件语句
     */
    lateinit var dailyDate: String
    private lateinit var mealTime: String
    lateinit var where: String

    override val layoutId: Int
        get() = R.layout.fragment_daily_mealtime

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }


    override fun initView() {
        super.initView()
        if (prefs.role == "厨师") {
            isShowAdd = true
        }
        arguments?.let {
            dailyDate = it.getString("mealdate")
        }

        toolbar_daily_meal.title = "${dailyDate}菜单"
        mealTime = MealTime.Breakfast.time

        where = "{\"\$and\":[{\"mealTime\":\"$mealTime\"}" +
                ",{\"mealDate\":\"$dailyDate\"}]}"

        initEvent()


    }

    /**
     * 单选按钮事件：选择就餐时段
     */
    fun initEvent() {
        rg_meal_time.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.rb_breakfast_time -> {
                    mealTime = MealTime.Breakfast.time
                }
                R.id.rb_lunch_time -> {
                    mealTime = MealTime.Lunch.time
                }
                R.id.rb_dinner_time -> {
                    mealTime = MealTime.Dinner.time
                }
            }
            where = "{\"\$and\":[{\"mealTime\":\"$mealTime\"}" +
                    ",{\"mealDate\":\"$dailyDate\"}]}"
            viewModel.getDailyMealOfDate(where)
        }
        //增加菜单，需要判断是增加哪类菜品
        binding.dailyMeal.addDailyMeal = object : Consumer<CookKind> {

            override fun accept(t: CookKind) {
                val bundle = Bundle()
                bundle.putBoolean("isSelected", true)
                bundle.putString("mealDate", "$dailyDate")
                bundle.putString("mealTime", "$mealTime")
                when (t) {
                    ColdFood -> {
                        bundle.putString("cookcategory", ColdFood.kindName)
                    }
                    HotFood -> {
                        bundle.putString("cookcategory", HotFood.kindName)
                    }
                    FlourFood -> {
                        bundle.putString("cookcategory", FlourFood.kindName)
                    }
                    SoutPorri -> {
                        bundle.putString("cookcategory", SoutPorri.kindName)
                    }
                    Snackdetail -> {
                        bundle.putString("cookcategory", Snackdetail.kindName)
                    }
                }
                findNavController().navigate(R.id.searchCookBookFragment, bundle)
            }

        }
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity, PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (prefs.role == "厨师") {
            (activity as AppCompatActivity).setSupportActionBar(toolbar_daily_meal)
            setHasOptionsMenu(true)
        }

        viewModel = activity!!.getViewModel {
            CookBookViewModel(respository)
        }

        coldAdapter = getAdapter(viewModel.coldList)
        hotAdapter = getAdapter(viewModel.hotList)
        flourAdapter = getAdapter(viewModel.flourList)
        soupAdapter = getAdapter(viewModel.soupList)
        snackAdapter = getAdapter(viewModel.snackList)

        //观察需要刷新哪类菜品列表
        viewModel.refreshAdapter?.observe(viewLifecycleOwner) {
            when (it) {
                ColdFood.kindName -> {
                    (coldAdapter as BaseDataBindingAdapter).forceUpdate()
                }
                HotFood.kindName -> {
                    (hotAdapter as BaseDataBindingAdapter).forceUpdate()
                }
                FlourFood.kindName -> {
                    (flourAdapter as BaseDataBindingAdapter).forceUpdate()
                }
                SoutPorri.kindName -> {
                    (soupAdapter as BaseDataBindingAdapter).forceUpdate()
                }
                Snackdetail.kindName -> {
                    (snackAdapter as BaseDataBindingAdapter).forceUpdate()
                }
                "All" -> {
                    (coldAdapter as BaseDataBindingAdapter).forceUpdate()
                    (hotAdapter as BaseDataBindingAdapter).forceUpdate()
                    (flourAdapter as BaseDataBindingAdapter).forceUpdate()
                    (soupAdapter as BaseDataBindingAdapter).forceUpdate()
                    (snackAdapter as BaseDataBindingAdapter).forceUpdate()
                }

            }
        }

        viewModel.getDailyMealOfDate(where)
        if (prefs.role == "厨师") {

            initSwipeMenu()
        }

        viewModel.defUI.showDialog.observe(viewLifecycleOwner){
            androidx.appcompat.app.AlertDialog.Builder(context!!)
                .setMessage(it)
                .create()
                .show()
        }
    }

    /*
    根据传入的列表生成对应适配器
     */
    fun getAdapter(list: List<DailyMeal>): BaseDataBindingAdapter<DailyMeal, LayoutMealItemBinding> {
        return BaseDataBindingAdapter(
            layoutId = R.layout.layout_meal_item,
            dataSource = { list },
            dataBinding = { LayoutMealItemBinding.bind(it) },
            callback = { dailyMeal, binding, position ->
                binding.dailymeal = dailyMeal
                //只允许厨师修改菜单
                if (prefs.role == "厨师") {
                    binding.clickEvent = object : Consumer<DailyMeal> {
                        override fun accept(t: DailyMeal) {
                            //点击是否选择
                            AlertDialog.Builder(context)
                                .setMessage("是否选择为教职工餐?")
                                .setPositiveButton("是") { dialog, which ->
                                    dailyMeal.isOfTeacher = true
                                    viewModel.updateDailyMeal(
                                        UpdateIsteacher(true),
                                        dailyMeal.objectId
                                    )
                                    dialog.dismiss()
                                    viewModel.setRefreshAdapter(dailyMeal)
                                }
                                .setNegativeButton("否") { dialog, which ->
                                    dailyMeal.isOfTeacher = false
                                    viewModel.updateDailyMeal(
                                        UpdateIsteacher(false),
                                        dailyMeal.objectId
                                    )
                                    dialog.dismiss()
                                    viewModel.setRefreshAdapter(dailyMeal)
                                }.create().show()
                        }
                    }
                }
                binding.onLongClick = object : Consumer<DailyMeal> {
                    override fun accept(t: DailyMeal) {
                        launch {
                            var text = ""
                            var goodsList = respository.getCookBookWithGoods(t.cookBook.objectId)
                            goodsList.goods.forEach {
                                text = text + it.goodsName + ","
                            }
                            //弹出原材料对话框
                            AlertDialog.Builder(context)
                                .setMessage("主料是：$text")
                                .create().show()
                        }
                    }

                }
            })
    }

    /**
     * 初始化Item侧滑菜单
     */
    private fun initSwipeMenu() {
        /*
        1、生成子菜单，这里将子菜单设置在右侧
         */
        val mSwipeMenuCreator = SwipeMenuCreator { leftMenu, rightMenu, position ->
            val deleteItem = SwipeMenuItem(context)
                .setBackground(color.colorAccent)
                .setText("删除")
                .setHeight(LayoutParams.MATCH_PARENT)
                .setWidth(150)
            rightMenu.addMenuItem(deleteItem)

        }
        /*
         2、关联RecyclerView，设置侧滑菜单
         */
        rlw_cold_food.setSwipeMenuCreator(mSwipeMenuCreator)
        rlw_hot_food.setSwipeMenuCreator(mSwipeMenuCreator)
        rlw_flour_food.setSwipeMenuCreator(mSwipeMenuCreator)
        rlw_soup_porri.setSwipeMenuCreator(mSwipeMenuCreator)
        rlw_snack_detail.setSwipeMenuCreator(mSwipeMenuCreator)
        /*
        3、定义子菜单点击事件
         */
        val mColdItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (menuBridge.position) {
                0 -> {
                    deleteDialog(viewModel.coldList[adapterPosition])
                }

            }
        }
        val mHotItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (menuBridge.position) {
                0 -> {
                    deleteDialog(viewModel.hotList[adapterPosition])
                }

            }
        }
        val mFlourItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (menuBridge.position) {
                0 -> {
                    deleteDialog(viewModel.flourList[adapterPosition])
                }

            }
        }
        val mSoupItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (menuBridge.position) {
                0 -> {
                    deleteDialog(viewModel.soupList[adapterPosition])
                }

            }
        }
        val mSnackItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (menuBridge.position) {
                0 -> {
                    deleteDialog(viewModel.snackList[adapterPosition])
                }

            }
        }
        /*
        4、给RecyclerView添加监听器
         */
        rlw_cold_food.setOnItemMenuClickListener(mColdItemMenuClickListener)
        rlw_hot_food.setOnItemMenuClickListener(mHotItemMenuClickListener)
        rlw_flour_food.setOnItemMenuClickListener(mFlourItemMenuClickListener)
        rlw_soup_porri.setOnItemMenuClickListener(mSoupItemMenuClickListener)
        rlw_snack_detail.setOnItemMenuClickListener(mSnackItemMenuClickListener)
    }

    private fun deleteDialog(dailyMeal: DailyMeal) {
        AlertDialog.Builder(context)
            .setMessage("确定要删除\"${dailyMeal.cookBook.foodName}\"吗?")
            .setNegativeButton("否") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("是") { _, _ ->
                viewModel.deleteDailyMeal(dailyMeal.objectId)
                when (dailyMeal.cookBook.foodCategory) {
                    ColdFood.kindName -> {
                        viewModel.coldList.remove(dailyMeal)
                        coldAdapter!!.forceUpdate()
                    }
                    HotFood.kindName -> {
                        viewModel.hotList.remove(dailyMeal)
                        hotAdapter!!.forceUpdate()
                    }
                    SoutPorri.kindName -> {
                        viewModel.soupList.remove(dailyMeal)
                        soupAdapter!!.forceUpdate()
                    }
                    FlourFood.kindName -> {
                        viewModel.flourList.remove(dailyMeal)
                        flourAdapter!!.forceUpdate()
                    }
                    Snackdetail.kindName -> {
                        viewModel.snackList.remove(dailyMeal)
                        snackAdapter!!.forceUpdate()
                    }
                }
            }.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_copy_daily_meal, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.copy_daily_meal -> {
                showDatePickerDialog(activity, 0, Calendar.getInstance())
            }
            R.id.create_daily_meal -> {
                verifyStoragePermissions(activity)
                launch {

                    viewModel.createStyledTable(dailyDate, context?.assets!!.open("菜谱模板.doc"))
                }

            }
            R.id.delete_daily_meal -> {
                AlertDialog.Builder(context)
                    .setMessage("确定要删除这天的菜单吗？")
                    .setNegativeButton("否") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("是") { dialog, _ ->
                        launch {
                            viewModel.deleteDailyMealOfDate(dailyDate)
                        }
                        dialog.dismiss()
                    }
                    .create().show()

            }
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
        DatePickerDialog(activity,
            themeResId,
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // 绑定监听器(How the parent is notified that the date is set.)
                // 此处得到选择的时间，可以进行你想要的操作
                var month = if (monthOfYear + 1 < 10) {
                    "0${monthOfYear + 1}"
                } else {
                    "${monthOfYear + 1}"
                }
                var day = if (dayOfMonth < 10) {
                    "0${dayOfMonth}"
                } else {
                    "$dayOfMonth"
                }
                var copyDate = "$year-$month-$day"
                viewModel.copyDailyMeal(dailyDate, copyDate)

            } // 设置初始日期
            ,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]).show()
    }


}