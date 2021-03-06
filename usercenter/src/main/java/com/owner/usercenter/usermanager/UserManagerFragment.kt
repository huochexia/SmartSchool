package com.owner.usercenter.usermanager

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import com.google.android.material.textfield.TextInputEditText
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.arouter.RouterPath
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.functional.Consumer
import com.owner.basemodule.room.entities.User
import com.owner.basemodule.widgets.charsidebar.WaveSideBar
import com.owner.usercenter.R
import com.owner.usercenter.databinding.FragmentManageUserBinding
import com.owner.usercenter.databinding.LayoutUserItemBinding
import com.owner.usercenter.util.TitleItemDecoration
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_manage_user.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class UserManagerFragment : BaseFragment<FragmentManageUserBinding>() {

    val prefs by instance<PrefsHelper>()

    override val layoutId: Int
        get() = R.layout.fragment_manage_user
    override val kodein: Kodein = Kodein.lazy {

        extend(parentKodein, copy = Copy.All)
        import(prefsModule)
    }
    val state = ObservableField<Int>()
    private val userList = mutableListOf<User>()
    private val repository by instance<UserManagerRepository>()
    private var viewModel: UserManagerViewModel? = null

    var adapter: BaseDataBindingAdapter<User, LayoutUserItemBinding>? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.getViewModel {
            UserManagerViewModel(repository)
        }
        adapter = BaseDataBindingAdapter(
            layoutId = R.layout.layout_user_item,
            dataSource = { userList },
            dataBinding = { LayoutUserItemBinding.bind(it) },
            callback = { user, binding, _ ->
                binding.user = user
                binding.onClick = object : Consumer<User> {
                    override fun accept(t: User) {
                        callPhone(t.mobilePhoneNumber!!)
                    }
                }
                binding.longClick = object : Consumer<User> {
                    override fun accept(t: User) {
                        managerDialog(t)
                    }
                }
            }

        )
        viewModel!!.getAllUsers()
            .doOnSubscribe{
                state.set(MultiStateView.VIEW_STATE_LOADING)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe { either ->
                either.fold({
                    state.set(MultiStateView.VIEW_STATE_ERROR)
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }, {
                    if (it.isEmpty()) {
                        state.set(MultiStateView.VIEW_STATE_EMPTY)
                    } else {
                        userList.clear()
                        userList.addAll(it)
                        userList.sortBy { user ->
                            user.letters
                        }
                        adapter!!.forceUpdate()
                        state.set(MultiStateView.VIEW_STATE_CONTENT)
                    }
                })

            }

        viewModel!!.defUI.showDialog.observe(viewLifecycleOwner) {
            AlertDialog.Builder(context)
                .setMessage(it)
                .create()
                .show()
        }
        //列表分隔线
        rlw_user_list.addItemDecoration(TitleItemDecoration(context!!, userList))

        add_user_fab.setOnClickListener {
            ARouter.getInstance().build(RouterPath.UserCenter.PATH_REGISTER).navigation()
        }
        char_side_bar.setOnTouchLetterChangeListener(object :
            WaveSideBar.OnTouchLetterChangeListener {
            override fun onLetterChange(letter: String) {

            }

        })
    }
    /****************************************************
     *长按事件；管理数据。修改和删除功能
     *****************************************************/
    private fun managerDialog(user: User) {
        val view = layoutInflater.inflate(com.goldenstraw.restaurant.R.layout.delete_or_update_dialog_view, null)
        val delete = view.findViewById<Button>(com.goldenstraw.restaurant.R.id.delete_action)
        delete.text = "删除"
        val update = view.findViewById<Button>(com.goldenstraw.restaurant.R.id.update_action)
        update.text = "修改"
        val managerDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        managerDialog.show()
        delete.setOnClickListener {
            deleteUsr("8a44cc1cae9df7257911bb7f7f949b34", user)
            managerDialog.dismiss()
        }
        update.setOnClickListener {
            updateUser("8a44cc1cae9df7257911bb7f7f949b34", user)
            managerDialog.dismiss()
        }
    }

    private fun deleteUsr(token: String, user: User) {
        val dialog = AlertDialog.Builder(context)
            .setMessage("确定要删除${user.username}吗?")
            .setPositiveButton("是") { dialog, _ ->
                viewModel!!.deleteUser(token, user.objectId)
                dialog.dismiss()
            }
            .setNegativeButton("否") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun updateUser(token: String, user: User) {
        val view = layoutInflater.inflate(R.layout.layout_user_info, null)
        val userName = view.findViewById<TextInputEditText>(R.id.tvNewUsername)
        userName.setText(user.username)
        val phoneNum = view.findViewById<TextInputEditText>(R.id.tvPhoneNumber)
        phoneNum.setText(user.mobilePhoneNumber)
//        val role = view.findViewById<RadioGroup>(R.id.rg_role)
//        val manager = view.findViewById<*>(R.id.manager_layout)
//        val chef = view.findViewById<*>(R.id.chefs_layout)
//        val direct = view.findViewById<*>(R.id.rg_district)
//        val category = view.findViewById<TextView>(R.id.tv_spinner_category)
//        when (user.role) {
//            "供应商" -> {
//                manager.visibility = View.GONE
//                chef.visibility = View.GONE
//                direct.visibility = View.GONE
//                category.visibility = View.VISIBLE
//            }
//            "管理员" -> {
//                manager.visibility = View.VISIBLE
//                chef.visibility = View.GONE
//                direct.visibility = View.GONE
//                category.visibility = View.GONE
//            }
//            "复核员" -> {
//                manager.visibility = View.VISIBLE
//                chef.visibility = View.GONE
//                direct.visibility = View.VISIBLE
//                category.visibility = View.GONE
//            }
//            "库管员" -> {
//                manager.visibility = View.VISIBLE
//                chef.visibility = View.GONE
//                direct.visibility = View.VISIBLE
//                category.visibility = View.GONE
//            }
//            "厨师" -> {
//                manager.visibility = View.GONE
//                chef.visibility = View.VISIBLE
//                direct.visibility = View.VISIBLE
//                category.visibility = View.GONE
//            }
//            "热菜主管" -> {
//                manager.visibility = View.GONE
//                chef.visibility = View.VISIBLE
//                direct.visibility = View.VISIBLE
//                category.visibility = View.GONE
//            }
//            "凉菜主管" -> {
//                manager.visibility = View.GONE
//                chef.visibility = View.VISIBLE
//                direct.visibility = View.VISIBLE
//                category.visibility = View.GONE
//            }
//            "主食主管" -> {
//                manager.visibility = View.GONE
//                chef.visibility = View.VISIBLE
//                direct.visibility = View.VISIBLE
//                category.visibility = View.GONE
//            }
//        }
        val updateView = AlertDialog.Builder(context)
            .setView(view)
            .setTitle("修改用户信息")
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        updateView.show()
    }

    /******************************************************
     * 调用拨号界面
     ******************************************************/
    fun callPhone(phoneNum: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:$phoneNum")
        intent.data = data
        startActivity(intent)
    }
}