package com.goldenstraw.restaurant.goodsmanager.ui.check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentCheckOrderListBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.ObjectCheckGoods
import com.goldenstraw.restaurant.goodsmanager.http.entities.OrderItem
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.uber.autodispose.autoDisposable
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_check_order_list.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.text.DecimalFormat

class CheckOrderListFragment : BaseFragment<FragmentCheckOrderListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_check_order_list

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    private val repository: VerifyAndPlaceOrderRepository by instance()

    var viewModel: VerifyAndPlaceOrderViewModel? = null
    var adapter: BaseDataBindingAdapter<OrderItem, LayoutOrderItemBinding>? = null
    var orderList = mutableListOf<OrderItem>()
    var supplier = ""
    var orderDate = ""
    var state = 0
    var district = 0
    override fun initView() {
        super.initView()
        supplier = arguments!!.getString("supplier")
        orderDate = arguments!!.getString("orderDate")
        state = arguments!!.getInt("orderState")
        district = arguments!!.getInt("district")
        initSwipeMenu()
        check_order_toolbar.title = supplier
        check_order_toolbar.subtitle = orderDate
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel {
            VerifyAndPlaceOrderViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_order_item,
            dataSource = { orderList },
            dataBinding = { LayoutOrderItemBinding.bind(it) },
            callback = { order, binding, position ->
                binding.orderitem = order
                binding.clickEvent = object : Consumer<OrderItem> {
                    override fun accept(t: OrderItem) {
                        //弹出修改数量的窗口
                        if (t.state == 1)
                            popUpCheckQuantityDialog(t)
                    }

                }
            }

        )
        getOrderItemList()
    }

    /**
     * 弹出确认实际数量的窗口
     */
    fun popUpCheckQuantityDialog(orderItem: OrderItem) {
        val view =
            LayoutInflater.from(context).inflate(R.layout.only_input_number_dialog_view, null)
        val edit = view.findViewById<EditText>(R.id.number_edit)
        val dialog = AlertDialog.Builder(context!!)
            .setTitle("确定\"${orderItem.goodsName}\"的数量")
            .setIcon(R.mipmap.add_icon)
            .setView(view)
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                val check = edit.text.toString().trim().toFloat()
                saveCheckResult(check, orderItem)
                dialog.dismiss()
            }.create()
        dialog.show()

    }


    /**
     * 查看状态由菜单控制，可以是1送货状态，也可以2已验状态。
     */
    private fun getOrderItemList() {
        val where =
            "{\"\$and\":[{\"supplier\":\"$supplier\"},{\"orderDate\":\"$orderDate\"}" +
                    ",{\"state\":$state},{\"district\":$district}" +
                    ",{\"quantity\":{\"\$ne\":0}}]}"
        viewModel!!.getAllOrderOfDate(where)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                orderList.clear()
                orderList.addAll(it)
                adapter!!.forceUpdate()
            }, {}, {})

    }

    /**
     * 初始化Item侧滑菜单
     */
    private fun initSwipeMenu() {
        /*
        1、生成子菜单，这里将子菜单设置在右侧
         */
        val mSwipeMenuCreator = SwipeMenuCreator { leftMenu, rightMenu, position ->
            if (state == 1) {
                val deleteItem = SwipeMenuItem(context)
                    .setBackground(R.color.colorAccent)
                    .setText("退货")
                    .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                    .setWidth(200)
                leftMenu.addMenuItem(deleteItem)
            }
            if (state == 2) {
                val againCheckItem = SwipeMenuItem(context)
                    .setBackground(R.color.colorAccent)
                    .setText("重验")
                    .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                    .setWidth(200)
                rightMenu.addMenuItem(againCheckItem)
            }

        }
        /*
         2、关联RecyclerView，设置侧滑菜单
         */
        rlw_check_order.setSwipeMenuCreator(mSwipeMenuCreator)
        /*
        3、定义子菜单点击事件
         */
        val mItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (direction) {
                1 -> {
                    when (menuBridge.position) {
                        0 -> {
                            val order = orderList[adapterPosition]
                            val dialog = AlertDialog.Builder(context!!)
                                .setTitle("确定\"${order.goodsName}\"退货吗？")
                                .setIcon(R.mipmap.add_icon)
                                .setNegativeButton("取消") { dialog, which ->
                                    dialog.dismiss()
                                }
                                .setPositiveButton("确定") { dialog, which ->
                                    returnedGoods(order)
                                    dialog.dismiss()
                                }.create()
                            dialog.show()
                        }
                    }
                }
                -1 -> {
                    when (menuBridge.position) {
                        0 -> {
                            if (orderList[adapterPosition].state == 3) {
                                Toast.makeText(context, "已经记帐不能重新验收！！", Toast.LENGTH_SHORT).show()
                                return@OnItemMenuClickListener
                            } else {
                                val dialog = AlertDialog.Builder(context!!)
                                    .setTitle("确定对\"${orderList[adapterPosition].goodsName}\"重新验收吗？")
                                    .setIcon(R.mipmap.add_icon)
                                    .setNegativeButton("取消") { dialog, which ->
                                        dialog.dismiss()
                                    }
                                    .setPositiveButton("确定") { dialog, which ->
                                        cancleChecked(orderList[adapterPosition])
                                        dialog.dismiss()
                                    }.create()
                                dialog.show()
                            }
                        }
                    }
                }
            }

        }
        /*
        4、给RecyclerView添加监听器
         */
        rlw_check_order.setOnItemMenuClickListener(mItemMenuClickListener)
    }

    /**
     * 保存验货结果
     */
    private fun saveCheckResult(
        check: Float,
        orderItem: OrderItem
    ) {
        val format = DecimalFormat(".00")
        val total = format.format(check * orderItem.unitPrice).toFloat()
        val newQuantity = ObjectCheckGoods(
            quantity = orderItem.quantity,
            checkQuantity = check,
            againCheckQuantity = check,
            total = total,
            state = 2
        )
        viewModel!!.setCheckQuantity(newQuantity, orderItem.objectId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                orderList.remove(orderItem)
                adapter!!.forceUpdate()
            }, {})
    }
    /**
     * 重验
     */
    private fun cancleChecked(orderItem: OrderItem) {
        val again = ObjectCheckGoods(orderItem.quantity, 0.0f, 0.0f, 0.0f, 1)
        viewModel!!.setCheckQuantity(again, orderItem.objectId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                orderList.remove(orderItem)
                adapter!!.forceUpdate()
            }, {})
    }

    /**
     * 退货,状态改为-1。
     */
    private fun returnedGoods(orderItem: OrderItem) {
        val returned = ObjectCheckGoods(orderItem.quantity, 0.0f, 0.0f, 0.0f, -1)
        viewModel!!.setCheckQuantity(returned, orderItem.objectId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                orderList.remove(orderItem)
                adapter!!.forceUpdate()
            }, {})

    }
}