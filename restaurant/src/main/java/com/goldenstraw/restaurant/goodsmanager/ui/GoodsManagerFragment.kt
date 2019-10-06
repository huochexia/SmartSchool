package com.goldenstraw.restaurant.goodsmanager.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentGoodsListBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsCategoryBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBindingImpl
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.OrderMgViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.BaseViewModelFactory
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
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
    var viewModel: OrderMgViewModel? = null
    var adapter: BaseDataBindingAdapter<Goods, LayoutGoodsItemBinding>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.getViewModel {
            OrderMgViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_goods_item,
            dataSource = { viewModel!!.goodsList },
            bindBinding = { LayoutGoodsItemBinding.bind(it) },
            callback = { category, binding, _ ->
                binding.goods = category
            }
        )
    }


}