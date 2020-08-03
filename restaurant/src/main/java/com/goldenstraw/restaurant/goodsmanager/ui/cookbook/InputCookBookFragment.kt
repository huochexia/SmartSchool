package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentInputCookBookBinding
import com.goldenstraw.restaurant.databinding.LayoutCookMainMaterialItemBinding
import com.goldenstraw.restaurant.goodsmanager.http.entities.CookBook
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.utils.CookKind
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CookBookViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.Goods
import kotlinx.android.synthetic.main.fragment_cookbook_detail.toolbar
import kotlinx.android.synthetic.main.fragment_input_cook_book.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * 录入菜谱
 */
class InputCookBookFragment : BaseFragment<FragmentInputCookBookBinding>() {

    lateinit var cookCategory: String

    override val layoutId: Int
        get() = R.layout.fragment_input_cook_book

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    private val repository by instance<CookBookRepository>()
    lateinit var viewModel: CookBookViewModel
    var adapter: BaseDataBindingAdapter<Goods, LayoutCookMainMaterialItemBinding>? = null

    override fun initView() {
        super.initView()
        arguments?.let {
            cookCategory = it.getString("cookcategory")
        }
        toolbar.title = cookCategory

        when (cookCategory) {
            CookKind.ColdFood.kindName, CookKind.HotFood.kindName -> {
                spinner_cook_kind.visibility = View.VISIBLE
            }
            CookKind.FlourFood.kindName, CookKind.SoutPorri.kindName -> {
                tv_kind_content.visibility = View.INVISIBLE
            }
            CookKind.Snackdetail.kindName -> {
                spinner_mingdang_kind.visibility = View.VISIBLE
            }
        }

        add_main_material.setOnClickListener {
            findNavController().navigate(R.id.searchMaterialFragment)
        }
        //放弃
        btn_cancel_cookbook.setOnClickListener {
            viewModel.materialList.clear()
            findNavController().popBackStack()
        }
        //保存菜谱,当菜名和原材料均不为空时，可以进行保存
        btn_save_cookbook.setOnClickListener {

            if (ed_cook_name.text.isNotEmpty() && viewModel.materialList.isNotEmpty()) {
                val kind = when (cookCategory) {
                    CookKind.ColdFood.kindName, CookKind.HotFood.kindName ->
                        spinner_cook_kind.selectedItem.toString()
                    CookKind.Snackdetail.kindName ->
                        spinner_mingdang_kind.selectedItem.toString()
                    else -> ""
                }
                val newFood = CookBook(
                    foodCategory = cookCategory,
                    foodName = ed_cook_name.text.toString(),
                    foodKind = kind,
                    material = viewModel.materialList
                )
                viewModel.createCookBook(newFood)
                findNavController().popBackStack()
            } else {
                AlertDialog.Builder(context!!)
                    .setMessage("菜名或材料不能为空！！").create().show()
            }

        }
        /*
        种类选择取值的另一种方式
         */
//        spinner_cook_kind.onItemSelectedListener = object : OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("not implemented")
//            }
//
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                activity!!.resources.getStringArray(R.array.cook_kind)[position]
//            }
//        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.getViewModel {
            CookBookViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_cook_main_material_item,
            dataSource = { viewModel.materialList },
            dataBinding = { LayoutCookMainMaterialItemBinding.bind(it) },
            callback = { goods, binding, position ->
                binding.goods = goods
                binding.clickEvent = object : Consumer<Goods> {
                    override fun accept(t: Goods) {
                        viewModel.materialList.remove(goods)
                        adapter!!.forceUpdate()
                    }
                }
            }
        )
        //刷新通知
        viewModel.defUI.refreshEvent.observe(viewLifecycleOwner) {
            adapter!!.forceUpdate()
        }

    }

}