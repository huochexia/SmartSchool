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
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class GoodsSearchFragment : BaseFragment<FragmentGoodsListBinding>() {
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(goodsDataSourceModule)
    }
    override val layoutId: Int
        get() = R.layout.fragment_search_goods
    private val repository: GoodsRepository by instance()
    var viewModel: OrderMgViewModel? = null
    var adapter: BaseDataBindingAdapter<Goods, LayoutGoodsItemBinding>? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.getViewModel {
            OrderMgViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_goods_item,
            dataSource = { viewModel!!.searchGoodsResultList },
            dataBinding = { LayoutGoodsItemBinding.bind(it) },
            callback = { goods, binding, _ ->
                binding.goods = goods
                binding.checkEvent = object : Consumer<Goods> {
                    override fun accept(t: Goods) {
                        t.isChecked = !t.isChecked
                        binding.cbGoods.isChecked = t.isChecked
                    }
                }
                binding.cbGoods.isChecked = goods.isChecked //这里设置的是初始状态
            }
        )
        viewModel!!.getIsRefresh().observe(this, Observer {
            if (it) {
                adapter!!.forceUpdate()
            }
        })
    }

    /**
     * 加入购物车
     */
    fun addGoodsToShoppingCart() {
        viewModel!!.addGoodsToShoppingCart(viewModel!!.searchGoodsResultList)
        //还原商品信息
        val selectedList = mutableListOf<Goods>()
        viewModel!!.searchGoodsResultList.forEach {
            if (it.isChecked) {
                it.isChecked = false
                it.quantity = 1
                selectedList.add(it)
            }
        }
        viewModel!!.searchGoodsResultList.removeAll(selectedList)
        adapter!!.forceUpdate()
    }
}