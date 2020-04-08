package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentDailyMealtimeBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.util.toast
import kotlinx.android.synthetic.main.fragment_daily_mealtime.*
import org.kodein.di.Copy
import org.kodein.di.Kodein

class DailyMealTimeFragment : BaseFragment<FragmentDailyMealtimeBinding>() {

    lateinit var dailyDate: String

    override val layoutId: Int
        get() = R.layout.fragment_daily_mealtime

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    override fun initView() {
        super.initView()
        arguments?.let {
            dailyDate = it.getString("mealdate")
        }
        toolbar.title = "${dailyDate}菜单"
    }


}