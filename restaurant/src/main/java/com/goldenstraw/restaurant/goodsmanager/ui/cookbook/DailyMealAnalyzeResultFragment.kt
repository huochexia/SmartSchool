package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentMealAnalyzeResultBindingImpl
import com.goldenstraw.restaurant.generated.callback.OnClickListener
import com.goldenstraw.restaurant.goodsmanager.http.entities.AnalyzeMealResult
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.utils.CookKind
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.getViewModel {
            CookBookViewModel(repository)
        }

        viewModel.statistcsDailyMeal(dateRange.toMutableList())
        viewModel.defUI.showDialog.observe(viewLifecycleOwner) {
            AlertDialog.Builder(context!!)
                .setMessage(it)
                .create()
                .show()
        }

    }

    fun onClickCookbook(view: View) {
        val bundle = Bundle()
        bundle.putString("startDate", startDate)
        bundle.putString("endDate", endDate)
        when (view.id) {
            R.id.cold_sucai->{
                bundle.putString("Category",CookKind.ColdFood.kindName)
                bundle.putString("Kind","素菜")
            }
            R.id.cold_xiaohun->{
                bundle.putString("Category",CookKind.ColdFood.kindName)
                bundle.putString("Kind","小荤菜")
            }
            R.id.cold_dahun->{
                bundle.putString("Category",CookKind.ColdFood.kindName)
                bundle.putString("Kind","大荤菜")
            }
            R.id.hot_sucai->{
                bundle.putString("Category",CookKind.HotFood.kindName)
                bundle.putString("Kind","素菜")
            }
            R.id.hot_xiaohun->{
                bundle.putString("Category",CookKind.HotFood.kindName)
                bundle.putString("Kind","小荤菜")
            }
            R.id.hot_dahun->{
                bundle.putString("Category",CookKind.HotFood.kindName)
                bundle.putString("Kind","大荤菜")
            }
            R.id.flour_mianshi->{
                bundle.putString("Category",CookKind.FlourFood.kindName)
                bundle.putString("Kind","面食")
            }
            R.id.flour_xianlei->{
                bundle.putString("Category",CookKind.FlourFood.kindName)
                bundle.putString("Kind","馅类")
            }
            R.id.flour_zaliang->{
                bundle.putString("Category",CookKind.FlourFood.kindName)
                bundle.putString("Kind","杂粮")
            }
            R.id.soup_tang->{
                bundle.putString("Category",CookKind.SoutPorri.kindName)
                bundle.putString("Kind","汤")
            }
            R.id.soup_zhou->{
                bundle.putString("Category",CookKind.SoutPorri.kindName)
                bundle.putString("Kind","粥")
            }
            R.id.snack_zhu->{
                bundle.putString("Category",CookKind.Snackdetail.kindName)
                bundle.putString("Kind","煮")
            }
            R.id.snack_jianchao->{
                bundle.putString("Category",CookKind.Snackdetail.kindName)
                bundle.putString("Kind","煎炒")
            }
            R.id.snack_youzha->{
                bundle.putString("Category",CookKind.Snackdetail.kindName)
                bundle.putString("Kind","油炸")
            }
        }
        findNavController().navigate(R.id.statisticsCookBookNumFragment,bundle)
    }
}