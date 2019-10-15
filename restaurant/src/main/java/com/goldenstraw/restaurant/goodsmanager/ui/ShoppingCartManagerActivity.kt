package com.goldenstraw.restaurant.goodsmanager.ui

import android.view.ViewGroup
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityShoppingCartManagerBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.databinding.LayoutShoppingCartItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.di.shoppingcartdatasource
import com.goldenstraw.restaurant.goodsmanager.repositories.ShoppingCartRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.ShoppingCartMgViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import kotlinx.android.synthetic.main.activity_shopping_cart_manager.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class ShoppingCartManagerActivity : BaseActivity<ActivityShoppingCartManagerBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_shopping_cart_manager
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(shoppingcartdatasource)
    }
    private val repository by instance<ShoppingCartRepository>()
    var viewModel: ShoppingCartMgViewModel? = null
    var adapter: BaseDataBindingAdapter<GoodsOfShoppingCart, LayoutShoppingCartItemBinding>? = null

    override fun initView() {
        super.initView()
        viewModel = getViewModel {
            ShoppingCartMgViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_shopping_cart_item,
            dataSource = { viewModel!!.goodsList },
            dataBinding = { LayoutShoppingCartItemBinding.bind(it) },
            callback = { value, binding, _ ->
                with(binding) {
                    goods = value

                }
            }
        )
        initSwipeMenu()
    }

    private fun initSwipeMenu() {
        /*
      1、生成子菜单，这里将子菜单设置在右侧
       */
        val mSwipeMenuCreator = SwipeMenuCreator { leftMenu, rightMenu, position ->
            val deleteItem = SwipeMenuItem(this)
                .setBackground(R.color.colorAccent)
                .setText("删除")
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setWidth(200)
            rightMenu.addMenuItem(deleteItem)
            val updateItem = SwipeMenuItem(this)
                .setBackground(R.color.secondaryColor)
                .setText("修改")
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setWidth(200)
            rightMenu.addMenuItem(updateItem)
        }
        /*
         2、关联RecyclerView，设置侧滑菜单
         */
        rlw_shopping_cart.setSwipeMenuCreator(mSwipeMenuCreator)
        /*
        3、定义子菜单点击事件
         */
        val mItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (menuBridge.position) {
                0 -> {

                }
                1 -> {

                }
            }
        }
        /*
        4、给RecyclerView添加监听器
         */
        rlw_shopping_cart.setOnItemMenuClickListener(mItemMenuClickListener)
    }
}