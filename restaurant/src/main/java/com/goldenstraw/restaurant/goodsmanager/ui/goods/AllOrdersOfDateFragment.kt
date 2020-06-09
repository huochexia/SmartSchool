package com.goldenstraw.restaurant.goodsmanager.ui.goods

import android.app.AlertDialog
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.EditText
import androidx.databinding.ObservableField
import androidx.lifecycle.observe
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.R.color
import com.goldenstraw.restaurant.databinding.FragmentAllOrdersOfDateBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.queryordersactivitymodule
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectQuantityAndNote
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import com.uber.autodispose.autoDisposable
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_all_orders_of_date.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class AllOrdersOfDateFragment : BaseFragment<FragmentAllOrdersOfDateBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_all_orders_of_date

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(queryordersactivitymodule)
    }

    private val repository by instance<QueryOrdersRepository>()

    var viewModel: QueryOrdersViewModel? = null

    var viewState = ObservableField<Int>()

    var adapter: BaseDataBindingAdapter<OrderItem, LayoutOrderItemBinding>? = null

    var orderList = mutableListOf<OrderItem>()

    var orderDate = ""
    override fun initView() {
        super.initView()

        arguments?.let {
            orderDate = it.getString("orderDate")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }

        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_order_item,
            dataSource = {
                orderList
            },
            dataBinding = { LayoutOrderItemBinding.bind(it) },
            callback = { order, binding, position ->
                binding.orderitem = order
            }

        )

        val where = "{\"orderDate\":\"$orderDate\"}"
        viewModel!!.getAllOfOrders(where)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                if (it.isEmpty()) {
                    viewState.set(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    viewState.set(MultiStateView.VIEW_STATE_CONTENT)
                    orderList = it
                    adapter!!.forceUpdate()
                }
            }, {
                viewState.set(MultiStateView.VIEW_STATE_ERROR)
            }, {

            }, {
                viewState.set(MultiStateView.VIEW_STATE_LOADING)
            })

        viewModel!!.defUI.refreshEvent.observe(viewLifecycleOwner) {

            adapter!!.forceUpdate()
        }

        initSwipeMenu()
    }

    private fun initSwipeMenu() {
        /*
      1、生成子菜单，这里将子菜单设置在右侧
       */
        val mSwipeMenuCreator = SwipeMenuCreator { leftMenu, rightMenu, position ->
            val deleteItem = SwipeMenuItem(context)
                .setBackground(color.colorAccent)
                .setText("删除")
                .setHeight(LayoutParams.MATCH_PARENT)
                .setWidth(200)
            rightMenu.addMenuItem(deleteItem)
            val updateItem = SwipeMenuItem(context)
                .setBackground(color.secondaryColor)
                .setText("修改")
                .setHeight(LayoutParams.MATCH_PARENT)
                .setWidth(200)
            rightMenu.addMenuItem(updateItem)
        }
        /*
         2、关联RecyclerView，设置侧滑菜单
         */
        rlw_order_of_all.setSwipeMenuCreator(mSwipeMenuCreator)
        /*
        3、定义子菜单点击事件
         */
        val mItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (menuBridge.position) {
                0 -> {
                    if (orderList[adapterPosition].state == 0)
                        popUPDeleteDialog(orderList[adapterPosition])
                }
                1 -> {
                    if (orderList[adapterPosition].state == 0)
                        updateDialog(orderList[adapterPosition])
                }
            }
        }
        /*
        4、给RecyclerView添加监听器
         */
        rlw_order_of_all.setOnItemMenuClickListener(mItemMenuClickListener)
    }

    /**
     * 弹出删除对话框
     */
    private fun popUPDeleteDialog(orders: OrderItem) {
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_alert_name)
            .setTitle("确定要删除吗！！")
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                deleteOrders(orders)
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    /**
     * 修改购物车商品信息，主要是数量和增加备注
     */
    private fun updateDialog(orders: OrderItem) {
        val view = layoutInflater.inflate(R.layout.edit_goods_of_shoppingcart_dialog_view, null)
        val goodsQuantity = view.findViewById<EditText>(R.id.et_goods_quantity)
        val goodsOfNote = view.findViewById<EditText>(R.id.et_goods_of_note)
        goodsQuantity.setText(orders.quantity.toString())
        goodsOfNote.setText(orders.note)

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
                    val newOrderItem = ObjectQuantityAndNote(quantity.toFloat(), note)
                    viewModel!!.updateOrderItem(newOrderItem, orders.objectId)
                    orders.note = note
                    orders.quantity = quantity.toFloat()
                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }

    //删除订单
    private fun deleteOrders(orders: OrderItem) {
        viewModel!!.deleteOrderItem(orders.objectId)
        orderList.remove(orders)
    }


}