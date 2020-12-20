package com.goldenstraw.restaurant.goodsmanager.ui.purchase

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentShoppingCarBinding
import com.goldenstraw.restaurant.databinding.LayoutShoppingCarBinding
import com.goldenstraw.restaurant.databinding.LayoutShoppingCarItemBinding
import com.goldenstraw.restaurant.goodsmanager.di.prefsModule
import com.goldenstraw.restaurant.goodsmanager.repositories.shoppingcar.ShoppingCarRepository
import com.goldenstraw.restaurant.goodsmanager.utils.CookKind
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.goldenstraw.restaurant.goodsmanager.viewmodel.ShoppingCarMgViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.FoodWithMaterialsOfShoppingCar
import com.owner.basemodule.room.entities.MaterialOfShoppingCar
import com.owner.basemodule.room.entities.NewOrder
import com.owner.basemodule.room.entities.materialToOrder
import kotlinx.android.synthetic.main.fragment_shopping_car.*
import kotlinx.coroutines.launch
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance


class ShoppingCarFragment : BaseFragment<FragmentShoppingCarBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_shopping_car
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(prefsModule)
    }
    private val repository by instance<ShoppingCarRepository>()
    var viewModel: ShoppingCarMgViewModel? = null

    var foodAdapter: BaseDataBindingAdapter<FoodWithMaterialsOfShoppingCar, LayoutShoppingCarBinding>? =
        null

    private val prefs by instance<PrefsHelper>()


    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        viewModel = activity!!.getViewModel {
            ShoppingCarMgViewModel(repository)
        }
        foodAdapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_shopping_car,
            dataSource = {
                viewModel!!.categoryList
            },
            dataBinding = { LayoutShoppingCarBinding.bind(it) },
            callback = { value, binding, _ ->
                binding.foodAndMaterial = value

                val materialAdapter = BaseDataBindingAdapter(
                    layoutId = R.layout.layout_shopping_car_item,
                    dataBinding = { LayoutShoppingCarItemBinding.bind(it) },
                    dataSource = { value.materials },
                    callback = { material, bind, _ ->
                        bind.material = material
                        bind.checkEvent = object : Consumer<MaterialOfShoppingCar> {
                            override fun accept(t: MaterialOfShoppingCar) {
                                updateDialog(t)
                            }

                        }
                        bind.onLongClick = object : Consumer<MaterialOfShoppingCar> {
                            override fun accept(t: MaterialOfShoppingCar) {
                                popUPDeleteDialog(t)
                            }
                        }
                    }
                )
                binding.rlcMaterialList.adapter = materialAdapter
            }
        )

        viewModel!!.defUI.refreshEvent.observe(viewLifecycleOwner) {
            foodAdapter!!.forceUpdate()
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.cold_food -> {
                viewModel!!.groupByFoodCategory(CookKind.ColdFood.kindName)
                toolbar.subtitle = CookKind.ColdFood.kindName
            }
            R.id.hot_food -> {
                viewModel!!.groupByFoodCategory(CookKind.HotFood.kindName)
                toolbar.subtitle = CookKind.HotFood.kindName
            }
            R.id.flour_food -> {
                viewModel!!.groupByFoodCategory(CookKind.FlourFood.kindName)
                toolbar.subtitle = CookKind.FlourFood.kindName
            }
            R.id.soup_food -> {
                viewModel!!.groupByFoodCategory(CookKind.SoutPorri.kindName)
                toolbar.subtitle = CookKind.SoutPorri.kindName
            }
            R.id.snack_food -> {
                viewModel!!.groupByFoodCategory(CookKind.Snackdetail.kindName)
                toolbar.subtitle = CookKind.Snackdetail.kindName
            }
            R.id.common_food -> {
                viewModel!!.groupByFoodCategory("通用")
                toolbar.subtitle = "通用"
            }
        }
    }

    /**
     * 弹出删除对话框
     */
    private fun popUPDeleteDialog(material: MaterialOfShoppingCar) {
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_alert_name)
            .setTitle("确定要删除--${material.goodsName}?")
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                launch {
                    viewModel!!.deleteMaterialOfShoppingCar(material)
                    foodAdapter!!.forceUpdate()
                    dialog.dismiss()
                }

            }.create()
        dialog.show()
    }

    /**
     * 修改购物车商品信息，主要是数量和增加备注
     */
    private fun updateDialog(material: MaterialOfShoppingCar) {
        val view = layoutInflater.inflate(R.layout.edit_goods_of_shoppingcart_dialog_view, null)
        val goodsQuantity = view.findViewById<EditText>(R.id.et_goods_quantity)
        val goodsOfNote = view.findViewById<EditText>(R.id.et_goods_of_note)
        goodsQuantity.setText(material.quantity.toString())
        goodsOfNote.setText(material.note)
        val dialog = AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_update_name)
            .setTitle("修改-${material.goodsName}")
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
                    material.quantity = quantity.toFloat()
                    material.note = note
                    launch {
                        viewModel!!.updateMaterialOfShoppingCar(material)
                        foodAdapter!!.forceUpdate()
                        dialog.dismiss()
                    }
                }
            }.create()
        dialog.show()
    }


    fun createNewOrder() {
        val materialList = viewModel!!.categoryList.first().materials
        materialOfShoppingCarToNewOrder(materialList, prefs.district)

    }

    /**
     * 将汇总的材料转换成订单保存，同时清空购物车
     */
    private fun materialOfShoppingCarToNewOrder(
        materialList: List<MaterialOfShoppingCar>,
        district: Int
    ) {
        val newOrderList = mutableListOf<NewOrder>()
        materialList.forEach {
            newOrderList.add(materialToOrder(it, district))
        }
        launch {
            viewModel!!.createNewOrder(newOrderList)
            findNavController().navigate(R.id.localNewOrderFragment)
        }
    }
    /**
     * 新版本清空购物车
     */
    private fun clearShoppingCar() {
        viewModel!!.clearShopping()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_check_subscribe, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.collect_all_goods -> {

                ll_food_category_btn.visibility = View.GONE
                btn_commit_shopping_cart.visibility = View.VISIBLE

                toolbar.subtitle = "汇总结果"
                viewModel!!.collectAllOfFoodCategory()
            }
            R.id.already_subscribe -> {

                findNavController().navigate(R.id.checkSubscribFragment)
            }
            R.id.query_new_order -> {

                findNavController().navigate(R.id.localNewOrderFragment)
            }
            R.id.clear_shoppingcar -> {
                clearShoppingCar()
                foodAdapter!!.forceUpdate()
            }
        }
        return true
    }

}