package com.owner.usercenter.usermanager

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.kennyc.view.MultiStateView
import com.owner.basemodule.adapter.BaseDataBindingAdapter
import com.owner.basemodule.arouter.RouterPath
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.base.viewmodel.getViewModel
import com.owner.basemodule.room.entities.User
import com.owner.usercenter.R
import com.owner.usercenter.databinding.FragmentManageUserBinding
import com.owner.usercenter.databinding.LayoutUserItemBinding
import com.uber.autodispose.autoDisposable
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
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
    private val repository :UserManagerRepository by instance()
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
            callback = { user, binding, position ->
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
                        adapter!!.forceUpdate()
                        state.set(MultiStateView.VIEW_STATE_CONTENT)
                    }
                })

            }
        initSwipeMenu()
        add_user_fab.setOnClickListener {
            ARouter.getInstance().build(RouterPath.UserCenter.PATH_REGISTER).navigation()
        }
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
                .setBackground(com.goldenstraw.restaurant.R.color.common_yellow)
                .setText("删除")
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setWidth(150)
           leftMenu.addMenuItem(deleteItem)
            val updateItem = SwipeMenuItem(context)
                .setBackground(com.goldenstraw.restaurant.R.color.colorAccent)
                .setText("修改")
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setWidth(150)
            leftMenu.addMenuItem(updateItem)
        }
        /*
         2、关联RecyclerView，设置侧滑菜单
         */
        rlw_user_list.setSwipeMenuCreator(mSwipeMenuCreator)
        /*
        3、定义子菜单点击事件
         */
        val mItemMenuClickListener = OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction  //用于得到是左侧还是右侧菜单，主要用于当两侧均有菜单时的判断
            when (menuBridge.position) {
                0 -> {

                }
                1 -> {

                }
            }
        }
        /*
        4、给RecyclerView添加监听器
         */
        rlw_user_list.setOnItemMenuClickListener(mItemMenuClickListener)
    }
}