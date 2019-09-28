package com.goldenstraw.restaurant.goodsmanager.ui

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentGoodsListBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.OrderMgViewModel
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
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
    //通过Fragment扩展函数创建ViewModel对象,如果ViewModel采用Kodein形式注入，需要上下文环境，这部分
    //掌握的还不太理想，所以暂是不考虑使用。
    private val viewModel: OrderMgViewModel = getViewModel { OrderMgViewModel(repository) }


}