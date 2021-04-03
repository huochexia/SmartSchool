package com.owner.usercenter.usermanager

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.arouter.RouterPath
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
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
    override val layoutId: Int
        get() = R.layout.fragment_manage_user
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    val state = ObservableField<Int>()
    private val userList = mutableListOf<User>()
    private val repository by instance<UserManagerRepository>()
    private var viewModel: UserManagerViewModel?=null

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

            }

        )
        viewModel!!.getAllUsers().subscribeOn(Schedulers.io())
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
        val managerDialog = android.app.AlertDialog.Builder(context)
            .setView(view)
            .create()
        managerDialog.show()
        delete.setOnClickListener {

            managerDialog.dismiss()
        }
        update.setOnClickListener {

            managerDialog.dismiss()
        }
    }

}