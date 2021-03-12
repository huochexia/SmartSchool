package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityCookbookNavigationBinding
import com.goldenstraw.restaurant.goodsmanager.di.cookbookactivitymodule
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CookBookViewModel
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class CookBookMainActivity() : BaseActivity<ActivityCookbookNavigationBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_cookbook_navigation
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(cookbookactivitymodule)
    }
    private val respository by instance<CookBookRepository>()

    lateinit var viewModel: CookBookViewModel

    override fun initView() {
        super.initView()
        viewModel = getViewModel {
            CookBookViewModel(respository)
        }
        //同步数据
        viewModel.syncCookbook()
    }
}