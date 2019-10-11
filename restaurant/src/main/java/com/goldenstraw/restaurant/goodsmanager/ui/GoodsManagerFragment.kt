package com.goldenstraw.restaurant.goodsmanager.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentGoodsListBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.OrderMgViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.Goods
import kotlinx.android.synthetic.main.fragment_goods_list.*
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
            dataBinding = { LayoutGoodsItemBinding.bind(it) },
            callback = { goods, binding, _ ->
                binding.goods = goods
                binding.checkEvent = object : Consumer<Goods> {
                    override fun accept(t: Goods) {
                        t.isChecked = !t.isChecked
                        binding.cbGoods.isChecked = t.isChecked
                    }
                }
                binding.cbGoods.isChecked = goods.isChecked
            }
        )
        viewModel!!.selected.observe(this, Observer {
            viewModel!!.getGoodsFromCategory(it)
            adapter!!.forceUpdate()
        })
        viewModel!!.isGoodsListRefresh.observe(this, Observer {
            if (it) {
                adapter!!.forceUpdate()
                rlw_goods_item.smoothScrollToPosition(adapter!!.itemCount)
            }

        })
    }


}