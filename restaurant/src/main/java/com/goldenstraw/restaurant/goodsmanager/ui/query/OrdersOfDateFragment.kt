package com.goldenstraw.restaurant.goodsmanager.ui.query

import android.app.AlertDialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentOrdersOfDateListBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectSupplier
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.queryorders.QueryOrdersRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.QueryOrdersViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.uber.autodispose.autoDisposable
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_orders_of_date_list.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class OrdersOfDateFragment : BaseFragment<FragmentOrdersOfDateListBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_orders_of_date_list
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
    }

    val repository: QueryOrdersRepository by instance()
    var viewModel: QueryOrdersViewModel? = null
    var adapter: BaseDataBindingAdapter<OrderItem, LayoutOrderItemBinding>? = null
    var orderList = mutableListOf<OrderItem>()
    lateinit var supplier: String
    lateinit var date: String
    override fun initView() {
        super.initView()
        supplier = arguments?.getString("supplier")!!
        date = arguments?.getString("date")!!

        toolbar.title = supplier
        toolbar.subtitle = date

        initSwipeMenu()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.getViewModel {
            QueryOrdersViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_order_item,
            dataSource = { orderList },
            dataBinding = { LayoutOrderItemBinding.bind(it) },
            callback = { order, binding, position ->
                binding.orderitem = order
            }

        )
        val where = "{\"\$and\":[{\"supplier\":\"$supplier\"},{\"orderDate\":\"$date\"}]}"

        viewModel!!.getOrdersOfSupplier(where)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                orderList.clear()
                orderList.addAll(it)
                adapter!!.forceUpdate()
            }, {

            }, {

            })

    }

    /**
     * 初始化Item侧滑菜单
     */
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
        }
        /*
         2、关联RecyclerView，设置侧滑菜单
         */
        rlw_order_of_supplier.setSwipeMenuCreator(mSwipeMenuCreator)
        /*
        3、定义子菜单点击事件
         */
        val mItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (menuBridge.position) {
                0 -> {
                    //当state=1时，属于送货阶段，可以进行供应商调整。
                    if (orderList[adapterPosition].state == 1)
                        deleteDialog(orderList[adapterPosition])
                    else
                        Toast.makeText(context, "该商品不是新订单，不能删除！！", Toast.LENGTH_SHORT).show()
                }

            }
        }
        /*
       4、给RecyclerView添加监听器
        */
        rlw_order_of_supplier.setOnItemMenuClickListener(mItemMenuClickListener)
    }

    /**
     * 删除对话框
     */

    private fun deleteDialog(order: OrderItem) {
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_alert_name)
            .setTitle("确定删除")
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                //将商品信息中的供应商清空，同时state设定为0，则该商品为订货最初状态
                val newOrder = ObjectSupplier("", 0)
                viewModel!!.updateOrderOfSupplier(newOrder, order.objectId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .autoDisposable(scopeProvider)
                    .subscribe({
                        orderList.remove(order)
                        adapter!!.forceUpdate()
                    }, {})
                dialog.dismiss()
            }.create()
        dialog.show()
    }
}