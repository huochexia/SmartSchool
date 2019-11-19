package com.owner.usercenter.usermanager

import arrow.core.Either
import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.User
import io.reactivex.Flowable


class UserManagerViewModel(
    private val repository: UserManagerRepository
) : BaseViewModel() {

    /*
      从本地获取所有用户
     */
    fun getAllUsers(): Flowable<Either<Errors, List<User>>> {
        return repository.getAllUsersList()
    }
}