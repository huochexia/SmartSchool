package com.goldenstraw.restaurant.ui

import androidx.navigation.fragment.NavHostFragment
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityOrderManagerBinding
import com.owner.basemodule.base.view.activity.BaseActivity
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
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.order_Manager_Fragment) as NavHostFragment? ?: return
    }
}
