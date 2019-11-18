package com.owner.usercenter.usermanager

import com.owner.basemodule.base.view.fragment.BaseFragment
import com.owner.usercenter.R
import com.owner.usercenter.databinding.FragmentManageUserBinding
import org.kodein.di.Copy
import org.kodein.di.Kodein

class UserManagerFragment : BaseFragment<FragmentManageUserBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_manage_user
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }
}