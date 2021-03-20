package com.goldenstraw.restaurant.goodsmanager.ui.purchase

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.lifecycle.observe
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentNeworderListBinding
import com.goldenstraw.restaurant.databinding.LayoutNeworderItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.prefsModule
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcar.ShoppingCarRepository
import com.goldenstraw.restaurant.goodsmanager.viewmodel.ShoppingCarMgViewModel
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.NewOrder
import kotlinx.android.synthetic.main.fragment_neworder_list.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 本地新订单
 */
class LocalNewOrderFragment : BaseFragment<FragmentNeworderListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_neworder_list

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(prefsModule)
    }
    private val repository by instance<ShoppingCarRepository>()
    var viewModel: ShoppingCarMgViewModel? = null

    var viewState = ObservableField<Int>()

    var adapter: BaseDataBindingAdapter<NewOrder, LayoutNeworderItemBinding>? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(new_order_toolbar)
        setHasOptionsMenu(true)

        viewModel = activity!!.getViewModel {
            ShoppingCarMgViewModel(repository)
        }

        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_neworder_item,
            dataSource = {
                viewModel!!.newOrderList
            },
            dataBinding = { LayoutNeworderItemBinding.bind(it) },
            callback = { order, binding, position ->
                binding.newOrder = order
                binding.clickEvent = object : Consumer<NewOrder> {
                    override fun accept(t: NewOrder) {
                       managerDialog(t)
                    }
                }

            }

        )

        viewModel!!.getLocalNewOrder()


        viewModel!!.defUI.refreshEvent.observe(viewLifecycleOwner) {
            adapter!!.forceUpdate()
            val size = viewModel!!.newOrderList.size
            new_order_toolbar.subtitle = "${size}项"
            if (size == 0) {
                viewState.set(MultiStateView.VIEW_STATE_EMPTY)
            } else {
                viewState.set(MultiStateView.VIEW_STATE_CONTENT)
            }
        }

    }
    /****************************************************
     *长按事件；管理数据。修改和删除功能
     *****************************************************/
    private fun managerDialog(orders: NewOrder) {
        val view = layoutInflater.inflate(R.layout.delete_or_update_dialog_view, null)
        val delete = view.findViewById<Button>(R.id.delete_action)
        delete.text = "删除"
        val update = view.findViewById<Button>(R.id.update_action)
        update.text="修改"
        val managerDialog = android.app.AlertDialog.Builder(context)
            .setView(view)
            .create()
        managerDialog.show()
        delete.setOnClickListener {
            deleteDialog(orders)
            managerDialog.dismiss()
        }
        update.setOnClickListener {
            updateDialog(orders)
            managerDialog.dismiss()
        }
    }


    /**
     * 弹出删除对话框
     */
    private fun deleteDialog(newOrder: NewOrder) {
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_alert_name)
            .setTitle("确定要删除-${newOrder.goodsName}吗！！")
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                viewModel!!.deleteNewOrder(newOrder)
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    /**
     * 修改购物车商品信息，主要是数量和增加备注
     */
    private fun updateDialog(newOrder: NewOrder) {
        val view = layoutInflater.inflate(R.layout.edit_goods_of_shoppingcart_dialog_view, null)
        val goodsQuantity = view.findViewById<EditText>(R.id.et_goods_quantity)
        val goodsOfNote = view.findViewById<EditText>(R.id.et_goods_of_note)
        goodsQuantity.setText(newOrder.quantity.toString())
        goodsOfNote.setText(newOrder.note)

        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_update_name)
            .setTitle("修改-${newOrder.goodsName}-信息")
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                val quantity = goodsQuantity.text.toString().trim()
                val note = goodsOfNote.text.toString().trim()
                if (quantity.isEmpty()) {
                    com.owner.basemodule.util.toast { "请填写必须内容！！" }
                } else {
                    newOrder.quantity = quantity.toFloat()
                    newOrder.note = note

                    viewModel!!.updateNewOrder(newOrder)


                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }

    /**
     * 提交订单到网络，形成正式订单
     */
    fun commitNewOrderToNet() {
        viewModel!!.commitNewOrderToRemote()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_new_order, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear_new_order -> {
                viewModel!!.clearAllNewOrder()
            }
        }
        return true
    }
}