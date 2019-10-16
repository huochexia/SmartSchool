package com.goldenstraw.restaurant.goodsmanager.ui

import android.app.AlertDialog
import android.content.Intent
import android.view.ViewGroup
import android.widget.EditText
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityShoppingCartManagerBinding
import com.goldenstraw.restaurant.databinding.LayoutGoodsItemBinding
import com.goldenstraw.restaurant.databinding.LayoutShoppingCartItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.goodsDataSourceModule
import com.goldenstraw.restaurant.goodsmanager.di.shoppingcartdatasource
import com.goldenstraw.restaurant.goodsmanager.repositories.ShoppingCartRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.ShoppingCartMgViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import com.owner.basemodule.util.toast
import com.uber.autodispose.autoDisposable
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
                    popUPDeleteDialog(adapterPosition)
                }
                1 -> {
                    updateDialog(viewModel!!.goodsList[adapterPosition])
                }
            }
        }
        /*
        4、给RecyclerView添加监听器
         */
        rlw_shopping_cart.setOnItemMenuClickListener(mItemMenuClickListener)
    }

    /**
     * 弹出删除对话框
     */
    private fun popUPDeleteDialog(adapterPosition: Int) {
        val dialog = AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_alert_name)
            .setTitle("确定要删除吗！！")
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                var list = mutableListOf<GoodsOfShoppingCart>()
                list.add(viewModel!!.goodsList[adapterPosition])
                deleteGoodsOfShoppingCartList(list)
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    /**
     * 修改购物车商品信息，主要是数量和增加备注
     */
    private fun updateDialog(goods: GoodsOfShoppingCart) {
        val view = layoutInflater.inflate(R.layout.edit_goods_of_shoppingcart_dialog_view, null)
        val goodsQuantity = view.findViewById<EditText>(R.id.et_goods_quantity)
        val goodsOfNote = view.findViewById<EditText>(R.id.et_goods_of_note)
        goodsQuantity.setText(goods.quantity.toString())
        goodsOfNote.setText(goods.note)
        val dialog = AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_update_name)
            .setTitle("修改商品信息")
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                val quantity = goodsQuantity.text.toString().trim()
                val note = goodsOfNote.text.toString().trim()
                if (quantity.isNullOrEmpty()) {
                    com.owner.basemodule.util.toast { "请填写必须内容！！" }
                } else {
                    goods.quantity = quantity.toInt()
                    goods.note = note
                    viewModel!!.updateGoodsOfShoppingCart(goods)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .autoDisposable(scopeProvider)
                        .subscribe()
                    adapter!!.forceUpdate()
                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }

    private fun deleteGoodsOfShoppingCartList(list: MutableList<GoodsOfShoppingCart>) {
        viewModel!!.deleteGoodsOfShoppingCartList(list)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                viewModel!!.goodsList.removeAll(list)
                if (viewModel!!.goodsList.isEmpty()) {
                    viewModel!!.state.set(MultiStateView.VIEW_STATE_ERROR)
                }
                adapter!!.forceUpdate()
            }, {})

    }

    /**
     * 返回当前购物车内数量
     */
    override fun onBackPressed() {
        var intent = Intent()
        intent.putExtra("quantity", viewModel!!.goodsList.size)
        this.setResult(1, intent)
        super.onBackPressed()
    }
}