package com.goldenstraw.restaurant.goodsmanager.ui

import androidx.lifecycle.ViewModelProviders
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentGoodsListBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.OrderMgViewModel
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.BaseViewModelFactory
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.util.toast
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class GoodsManagerFragment : BaseFragment<FragmentGoodsListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_goods_list

    override val kodein: Kodein = Kodein.lazy {

        extend(parentKodein, copy = Copy.All)

        import(goodsDataSourceModule)
    }
    //通过Kodein容器检索对象
    private val repository: GoodsRepository by instance()
    //使用同一个Activity范围下的共享ViewModel
    var viewModel: OrderMgViewModel?

    init {

        viewModel = activity?.getViewModel {
            OrderMgViewModel(repository)
        }
    }

}