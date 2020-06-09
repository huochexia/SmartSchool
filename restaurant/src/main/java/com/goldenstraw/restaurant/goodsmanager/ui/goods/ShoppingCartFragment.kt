package com.goldenstraw.restaurant.goodsmanager.ui.goods

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentShoppingCartBinding
import com.goldenstraw.restaurant.databinding.LayoutShoppingCartItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.prefsModule
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcart.ShoppingCartRepository
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.ShoppingCartMgViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import com.owner.basemodule.util.toast
import com.uber.autodispose.autoDisposable
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_shopping_cart.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance


class ShoppingCartFragment : BaseFragment<FragmentShoppingCartBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_shopping_cart
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(prefsModule)
    }
    private val repository by instance<ShoppingCartRepository>()
    var viewModel: ShoppingCartMgViewModel? = null
    var adapter: BaseDataBindingAdapter<GoodsOfShoppingCart, LayoutShoppingCartItemBinding>? = null

    private val prefs by instance<PrefsHelper>()

    override fun initView() {
        super.initView()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        viewModel = activity!!.getViewModel {
            ShoppingCartMgViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_shopping_cart_item,
            dataSource = {
                viewModel!!.goodsList
            },
            dataBinding = { LayoutShoppingCartItemBinding.bind(it) },
            callback = { value, binding, _ ->
                with(binding) {
                    goods = value
                    checkEvent = object : Consumer<GoodsOfShoppingCart> {
                        override fun accept(t: GoodsOfShoppingCart) {
                            t.isChecked = !t.isChecked
                            cbGoods.isChecked = t.isChecked
                        }

                    }
                    cbGoods.isChecked = value.isChecked
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
            val deleteItem = SwipeMenuItem(context)
                .setBackground(R.color.colorAccent)
                .setText("删除")
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setWidth(200)
            rightMenu.addMenuItem(deleteItem)
            val updateItem = SwipeMenuItem(context)
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
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_alert_name)
            .setTitle("确定要删除吗！！")
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                deleteGoodsOfShoppingCart(viewModel!!.goodsList[adapterPosition])
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
        val dialog = AlertDialog.Builder(context)
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
                    goods.quantity = quantity.toFloat()
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

    fun commitOrderItem() {
        val selectedList = mutableListOf<GoodsOfShoppingCart>()
        viewModel!!.goodsList.forEach { goods ->
            if (goods.isChecked) {
                selectedList.add(goods)
            }
        }
        commitAllGoodsOfShoppingCart(selectedList, prefs.district)
        cb_all.isChecked = false
    }

    /**
     * 提交所有选择的购物车商品到网络数据库
     */
    private fun commitAllGoodsOfShoppingCart(
        selectedList: MutableList<GoodsOfShoppingCart>,
        district: Int
    ) {
        viewModel!!.transGoodsOfShoppingCartToNewOrderItem(selectedList, district)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                viewModel!!.createNewOrderItem(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .autoDisposable(scopeProvider)
                    .subscribe({

                    }, {
                        toast { "批量处理购物车商品：" + it.message.toString() }
                    })

            }, {
                toast { "提交购物车商品：" + it.message.toString() }
            }, {
                //完成网络操作后，进行本地数据处理，从购物车中删除已加入订单的商品信息
                deleteGoodsOfShoppingCarList(selectedList)
            })
    }

    /**
     * 全选事件
     */
    fun allSelect() {
        if (cb_all.isChecked)
            viewModel!!.goodsList.forEach {
                it.isChecked = true
            }
        else
            viewModel!!.goodsList.forEach {
                it.isChecked = false
            }
        adapter!!.forceUpdate()
    }

    private fun deleteGoodsOfShoppingCarList(list: MutableList<GoodsOfShoppingCart>) {
        viewModel!!.deleteGoodsOfShoppingCartList(list)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                viewModel!!.goodsList.removeAll(list)
                if (viewModel!!.goodsList.isEmpty()) {
                    viewModel!!.state.set(MultiStateView.VIEW_STATE_EMPTY)
                }
                adapter!!.forceUpdate()
            }, {
                toast { "删除购物车内商品：" + it.message.toString() }
            })

    }

    private fun deleteGoodsOfShoppingCart(goods: GoodsOfShoppingCart) {
        viewModel!!.deleteGoodsOfShoppingCart(goods)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                viewModel!!.goodsList.remove(goods)
                adapter!!.forceUpdate()
            }, {})
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_check_subscribe, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.already_subscribe -> {

                findNavController().navigate(R.id.checkSubscribFragment)
            }
            R.id.clear_shoppingcar -> {
                deleteGoodsOfShoppingCarList(viewModel!!.goodsList)
                adapter!!.forceUpdate()
            }
        }
        return true
    }

}