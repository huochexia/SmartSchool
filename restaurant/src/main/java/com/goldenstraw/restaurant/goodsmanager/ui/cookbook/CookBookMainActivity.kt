package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityCookbookNavigationBinding
import com.goldenstraw.restaurant.goodsmanager.di.cookbookactivitymodule
import com.owner.basemodule.base.view.activity.BaseActivity
import org.kodein.di.Copy
import org.kodein.di.Kodein

class CookBookMainActivity() : BaseActivity<ActivityCookbookNavigationBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_cookbook_navigation
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(cookbookactivitymodule)
    }
}