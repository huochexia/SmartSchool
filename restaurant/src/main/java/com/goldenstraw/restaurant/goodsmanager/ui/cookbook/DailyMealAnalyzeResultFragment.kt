package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.os.Bundle
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentMealAnalyzeResultBindingImpl
import com.goldenstraw.restaurant.generated.callback.OnClickListener
import com.goldenstraw.restaurant.goodsmanager.http.entities.AnalyzeMealResult
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CookBookViewModel
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import kotlinx.android.synthetic.main.fragment_meal_analyze_result.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class DailyMealAnalyzeResultFragment : BaseFragment<FragmentMealAnalyzeResultBindingImpl>() {

    private var startDate: String = ""
    private var endDate: String = ""
    private var dateRange = arrayListOf<String>()

    private val repository by instance<CookBookRepository>()
    lateinit var viewModel: CookBookViewModel

    var results = AnalyzeMealResult()

    override val layoutId: Int
        get() = R.layout.fragment_meal_analyze_result

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    override fun initView() {

        super.initView()

        arguments?.run {
            startDate = getString("startDate")!!
            endDate = getString("endDate")!!
            dateRange = getStringArrayList("range")!!
        }
        meal_analyze_toolbar.title = "菜单综合分析"
        meal_analyze_toolbar.subtitle = "$startDate--$endDate"
        initEvent()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.getViewModel {
            CookBookViewModel(repository)
        }

        viewModel.statistcsDailyMeal(dateRange.toMutableList())


        viewModel.analyzeResult.observe(viewLifecycleOwner) { data ->
            results = data
        }
    }
    fun initEvent(){

    }
}