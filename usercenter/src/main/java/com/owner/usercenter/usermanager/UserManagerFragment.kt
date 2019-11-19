package com.owner.usercenter.usermanager

import android.os.Bundle
import androidx.databinding.ObservableField
import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.basemodule.room.entities.User
import com.owner.usercenter.R
import com.owner.usercenter.databinding.FragmentManageUserBinding
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import rx.Observable

class UserManagerFragment : BaseFragment<FragmentManageUserBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_manage_user
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
    val state = ObservableField<Int>()
    val userList = mutableListOf<User>()
    val viewModel: UserManagerViewModel by instance()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
}