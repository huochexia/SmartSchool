package com.goldenstraw.restaurant.goodsmanager.ui.supplier

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentSupplierCategoryGoodsBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.room.entities.Goods
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 用于显示某类商品的定价，便于供应商及时了解到所提供商品的价格信息。
 */
class CategoryGoodsInfoFragment : BaseFragment<FragmentSupplierCategoryGoodsBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_supplier_category_goods
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    private val repository: QueryOrdersRepository by instance()
    var viewModel: QueryOrdersViewModel? = null
    var adapter: BaseDataBindingAdapter<Goods, LayoutGoodsItemBinding>? = null
    var goodsList = mutableListOf<Goods>()
    var state = ObservableField<Int>()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_goods_item,
            dataBinding = { LayoutGoodsItemBinding.bind(it) },
            dataSource = { goodsList },
            callback = { goods, binding, position ->
                binding.goods = goods
                binding.addSub.visibility = View.INVISIBLE
                binding.cbGoods.visibility = View.INVISIBLE
            }
        )
        getGoodsOfCategory("f526b3f6e7")
    }

    /**
     * 获取商品信息
     */
    fun getGoodsOfCategory(categoryId: String) {
        val where = "{\"categoryCode\":\"$categoryId\"}"
        viewModel!!.getGoodsOfCategory(where)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                if (it.isEmpty()) {
                    state.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    state.set(MultiStateView.VIEW_STATE_CONTENT)
                }
                goodsList.clear()
                goodsList.addAll(it)
                adapter!!.forceUpdate()
            }, {
                state.set(MultiStateView.VIEW_STATE_ERROR)
            }, {}, {
                state.set(MultiStateView.VIEW_STATE_LOADING)
            })
    }


}