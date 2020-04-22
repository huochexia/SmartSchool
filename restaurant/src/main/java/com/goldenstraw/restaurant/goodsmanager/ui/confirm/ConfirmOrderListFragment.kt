package com.goldenstraw.restaurant.goodsmanager.ui.confirm

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentConfirmOrderListBinding
import com.goldenstraw.restaurant.databinding.LayoutOrderItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.*
import com.goldenstraw.restaurant.goodsmanager.repositories.place_order.VerifyAndPlaceOrderRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.VerifyAndPlaceOrderViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.uber.autodispose.autoDisposable
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_confirm_order_list.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class ConfirmOrderListFragment : BaseFragment<FragmentConfirmOrderListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_confirm_order_list

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    private val repository  by instance<VerifyAndPlaceOrderRepository>()

    var viewModel: VerifyAndPlaceOrderViewModel? = null
    var adapter: BaseDataBindingAdapter<OrderItem, LayoutOrderItemBinding>? = null
    var orderList = mutableListOf<OrderItem>()
    var supplier = ""
    var orderDate = ""
    var state = 2
    var district = 0
    override fun initView() {
        super.initView()
        supplier = arguments!!.getString("supplier")
        orderDate = arguments!!.getString("orderDate")
        district = arguments!!.getInt("district")
        state = arguments!!.getInt("orderState")
        initSwipeMenu()
        confirm_order_toolbar.title = supplier
        confirm_order_toolbar.subtitle = orderDate
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
            }

        )
        getOrderItemList()
        confirm_btn.setOnClickListener {
            transRecordState(orderList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .autoDisposable(scopeProvider)
                .subscribe {
                    viewModel!!.commitRecordState(it).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .autoDisposable(scopeProvider)
                        .subscribe {
                            orderList.forEach { order ->
                                order.state = 3
                            }
                            adapter!!.forceUpdate()

                        }
                }
        }

    }

    /**
     * 提交记帐
     */
    fun transRecordState(orderList: MutableList<OrderItem>): Observable<BatchOrdersRequest<ObjectState>> {
        return Observable.fromIterable(orderList)
            .map {
                var updateState = ObjectState(3)
                val batch = BatchOrderItem(
                    method = "PUT",
                    path = "/1/classes/OrderItem/${it.objectId}",
                    body = updateState
                )
                batch
            }
            .buffer(40)
            .map {
                val commit = BatchOrdersRequest(it)
                commit
            }
    }

    /**
     * 查看状态由菜单控制，2状态已验，3状态已确认，4状态已记帐
     */
     fun getOrderItemList() {
        //状态为2是只显示状态2的（已验未定），状态为3是显示状态大于等于3的，即已定或已记
        var where =""
        when (state) {
            2 -> {
                where =
                    "{\"\$and\":[{\"supplier\":\"$supplier\"},{\"orderDate\":\"$orderDate\"}" +
                            ",{\"state\":$state},{\"district\":$district}" +
                            ",{\"quantity\":{\"\$ne\":0}}]}"
            }
            3 -> {
                where =
                    "{\"\$and\":[{\"supplier\":\"$supplier\"},{\"orderDate\":\"$orderDate\"}" +
                            ",{\"state\":{\"\$gte\":$state}},{\"district\":$district}" +
                            ",{\"quantity\":{\"\$ne\":0}}]}"
            }
        }

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
        rlw_confirm_order.setSwipeMenuCreator(mSwipeMenuCreator)
        /*
        3、定义子菜单点击事件
         */
        val mItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (direction) {

                -1 -> {
                    when (menuBridge.position) {
                        0 -> {
                            if (orderList[adapterPosition].state == 3) {
                                Toast.makeText(context, "已经确认不能重新验收！！", Toast.LENGTH_SHORT).show()
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
        rlw_confirm_order.setOnItemMenuClickListener(mItemMenuClickListener)
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