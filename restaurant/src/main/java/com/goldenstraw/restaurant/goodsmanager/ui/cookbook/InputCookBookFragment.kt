package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentInputCookBookBinding
import com.goldenstraw.restaurant.databinding.LayoutCookMainMaterialItemBinding
import com.goldenstraw.restaurant.goodsmanager.adapter.KindSpinnerAdapter
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCookBook
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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

    private var spinnerList = mutableListOf<String>()

    override fun initView() {
        super.initView()
        arguments?.let {
            cookCategory = it.getString("cookcategory")
        }
        toolbar.title = cookCategory
        spinnerList = when (cookCategory) {
            CookKind.ColdFood.kindName, CookKind.HotFood.kindName -> mutableListOf(
                "素菜",
                "小荤菜",
                "大荤菜"
            )
            CookKind.FlourFood.kindName -> mutableListOf("面食", "杂粮")
            CookKind.SoutPorri.kindName -> mutableListOf("粥", "汤")
            CookKind.Snackdetail.kindName -> mutableListOf("煮", "煎炒", "油炸")
            else -> mutableListOf()
        }
        val adapter = KindSpinnerAdapter(context!!, spinnerList)

        spinner_cook_kind.adapter = adapter
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
                //根据菜谱种类，选择不同的分类
                val kind = spinner_cook_kind.selectedItem.toString()
                val newFood = NewCookBook(
                    foodCategory = cookCategory,
                    foodName = ed_cook_name.text.toString(),
                    foodKind = kind
                )
                /**
                 * 结构并发
                 * 这里启动一个协程非常必要，即使createCook()这个方法本身不是挂起函数。
                 * 因为在createCookBook（）方法中一定是有一个协程来处理数据工作的，也
                 * 就是里面有挂起函数，它不会阻塞当前进程。这样findNavController会继
                 * 续运行，此时保存数据可能还没有完成，于是在返回前一Fragment时，它无法
                 * 得到新加入的数据。所以要把createCook（）定义为挂起函数，将其与返回方
                 * 法findNavController置与同一协程当中。
                 *
                 */
                launch {
                    coroutineScope {
                        viewModel.createCookBook(newFood)
                        findNavController().popBackStack()
                    }
                }


            } else {
                AlertDialog.Builder(context!!)
                    .setMessage("菜名或材料不能为空！！").create().show()
            }

        }

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