package com.goldenstraw.restaurant.goodsmanager.ui.cookbook

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog.Builder
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentInputCookBookBinding
import com.goldenstraw.restaurant.databinding.LayoutCookMainMaterialItemBinding
import com.goldenstraw.restaurant.goodsmanager.adapter.KindSpinnerAdapter
import com.goldenstraw.restaurant.goodsmanager.http.entities.RemoteCookBook
import com.goldenstraw.restaurant.goodsmanager.repositories.cookbook.CookBookRepository
import com.goldenstraw.restaurant.goodsmanager.utils.CookKind
import com.goldenstraw.restaurant.goodsmanager.viewmodel.CookBookViewModel
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.LocalCookBook
import com.owner.basemodule.room.entities.Material
import kotlinx.android.synthetic.main.fragment_cookbook_detail.toolbar
import kotlinx.android.synthetic.main.fragment_input_cook_book.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import kotlin.properties.Delegates

/**
 * 录入菜谱
 */
class InputCookBookFragment : BaseFragment<FragmentInputCookBookBinding>() {

    lateinit var cookCategory: String

    private var isUpdate by Delegates.notNull<Boolean>()

    lateinit var cookbook: LocalCookBook

    val tempMaterials = mutableListOf<Material>() //临时保存材料的列表，用于修改状态下最后的删除

    override val layoutId: Int
        get() = R.layout.fragment_input_cook_book

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    private val repository by instance<CookBookRepository>()
    lateinit var viewModel: CookBookViewModel
    var adapter: BaseDataBindingAdapter<Material, LayoutCookMainMaterialItemBinding>? = null

    private var spinnerList = mutableListOf<String>()

    override fun initView() {
        super.initView()
        arguments?.let {
            cookCategory = it.getString("cookcategory")!!
            isUpdate = it.getBoolean("isUpdate")
            if (isUpdate) {
                cookbook = it.getSerializable("food") as LocalCookBook
                ed_cook_name.setText(cookbook.foodName)
                check_isStandby.isChecked = (cookbook.isStandby)
            }
        }
        toolbar.title = cookCategory
        spinnerList = when (cookCategory) {
            CookKind.ColdFood.kindName, CookKind.HotFood.kindName -> mutableListOf(
                "素菜",
                "小荤菜",
                "大荤菜"
            )
            CookKind.FlourFood.kindName -> mutableListOf("面食", "馅类", "杂粮")
            CookKind.SoutPorri.kindName -> mutableListOf("粥", "汤")
            CookKind.Snackdetail.kindName -> mutableListOf("煮", "煎炒", "油炸")
            else -> mutableListOf()
        }
        val adapter = KindSpinnerAdapter(context!!, spinnerList)

        spinner_cook_kind.adapter = adapter
        //如果是修改状态，则显示原值
        if (isUpdate) {
            spinner_cook_kind.setSelection(spinnerList.indexOf(cookbook.foodKind))
        }

        add_main_material.setOnClickListener {
            findNavController().navigate(R.id.searchMaterialFragment)
        }

        //放弃事件
        btn_cancel_cookbook.setOnClickListener {
            viewModel.materialList.clear()
            tempMaterials.clear()
            findNavController().popBackStack()
        }
        //保存菜谱,当菜名和原材料均不为空时，可以进行保存
        //saveCookBook中启动了协程，这样会在其没有完成时，就执行了findNavController
        btn_save_cookbook.setOnClickListener {

            launch {
                coroutineScope {
                    if (isUpdate) { //如果是修改状态，
                        updateCookBook()

                    } else {
                        saveCookBook()

                    }
                    findNavController().popBackStack()
                }

            }

        }

    }

    /*
     新增状态下，保存新的菜谱
     */
    private fun saveCookBook() {
        if (ed_cook_name.text.isNotEmpty() && viewModel.materialList.isNotEmpty()) {
            //根据菜谱种类，选择不同的分类
            val kind = spinner_cook_kind.selectedItem.toString()
            val newFood = RemoteCookBook(
                foodCategory = cookCategory,
                foodName = ed_cook_name.text.toString(),
                foodKind = kind,
                material = viewModel.materialList,
                isStandby = check_isStandby.isChecked

            )
            //保存过程是，先将不含原材料列表的菜谱保存在网络，然后获取它的objectId,将这个
            //ObjectId加入Material对应属性，然后将原材料列表加入菜谱，再次保存网络。
            //另一种思路：给菜谱多定义一个主键，用这个主键与原材料关联

            viewModel.createCookBook(newFood)

        } else {
            Builder(context!!)
                .setMessage("菜名或材料不能为空！！").create().show()
        }
    }

    /*
      修改状态下，获取isStandby，spinner的值,删除临时表中的材料
     */
    private suspend fun updateCookBook() {
        cookbook.foodName = ed_cook_name.text.toString()
        cookbook.isStandby = check_isStandby.isChecked
        cookbook.foodKind = spinner_cook_kind.selectedItem.toString()
        //因为新添加的材料，没有指定菜谱。
        viewModel.materialList.forEach {
            it.materialOwnerId = cookbook.objectId
        }
        viewModel.updateCookBook(cookbook, viewModel.materialList)

        viewModel.deleteMaterialOfCookBook(tempMaterials)

        tempMaterials.clear()

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
            callback = { material, binding, _ ->
                binding.material = material
                binding.clickEvent = object : Consumer<Material> {
                    override fun accept(t: Material) {
                        viewModel.materialList.remove(material)
                        //如果是修改，则需要将其暂存起来，以便保存时将其删除
                        if (isUpdate) {
                            tempMaterials.add(material)
                        }
                        adapter!!.forceUpdate()
                    }
                }
                binding.update = object : Consumer<Material> {
                    override fun accept(t: Material) {
                        //在这里弹出一个修改窗口
                        updateDialog(material)
                    }
                }
            }
        )
        //刷新通知
        viewModel.defUI.refreshEvent.observe(viewLifecycleOwner) {
            adapter!!.forceUpdate()
        }

    }

    /**
     * 修改购物车商品信息，主要是数量和增加备注
     */
    private fun updateDialog(material: Material) {
        val view = layoutInflater.inflate(R.layout.add_or_edit_one_dialog_view, null)
        val ration = view.findViewById<EditText>(R.id.dialog_edit)
        ration.setText(material.ration.toString())

        val dialog = android.app.AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_update_name)
            .setTitle("修改${material.goodsName}的配比")
            .setView(view)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                val quantity = ration.text.toString().trim()
                if (quantity.isNullOrEmpty()) {
                    com.owner.basemodule.util.toast { "请填写必须内容！！" }
                } else {
                    material.ration = quantity.toFloat()
                    adapter!!.forceUpdate()
                    dialog.dismiss()
                }
            }.create()
        dialog.show()
    }
}