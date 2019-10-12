package com.goldenstraw.restaurant.goodsmanager.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCategoryListBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsCategoryBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.OrderMgViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.GoodsCategory
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class CategoryManagerFragment : BaseFragment<FragmentCategoryListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_category_list
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(goodsDataSourceModule)
    }
    private val repository by instance<GoodsRepository>()
    /*
      使用同一个Activity范围下的共享ViewModel
     */
    var viewModel: OrderMgViewModel? = null
    var adapter: BaseDataBindingAdapter<GoodsCategory, LayoutGoodsCategoryBinding>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.getViewModel {
            OrderMgViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_goods_category,
            dataBinding = { LayoutGoodsCategoryBinding.bind(it) },
            dataSource = { viewModel!!.categoryList },
            callback = { goodsCategory, binding, _ ->
                binding.apply {
                    category = goodsCategory
                    goodsEvent = object : Consumer<GoodsCategory> {
                        override fun accept(t: GoodsCategory) {
                            viewModel!!.selected.value = t
                            viewModel!!.categoryList.forEach {
                                it.isSelected = false
                            }
                            t.isSelected = true
                            adapter!!.forceUpdate()
                        }
                    }
                    tvCategoryName.isSelected = goodsCategory.isSelected
                    if (goodsCategory.isSelected) {
                        view.visibility = View.VISIBLE
                    } else {
                        view.visibility = View.INVISIBLE
                    }
                }

            }
        )
        viewModel!!.getIsRefresh().observe(this, Observer {
            if (it)
                adapter!!.forceUpdate()
        })
    }

}