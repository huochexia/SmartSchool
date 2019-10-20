package com.goldenstraw.restaurant.goodsmanager.repositories.place_order

import com.owner.basemodule.base.repository.ILocalDataSource
import com.owner.basemodule.room.AppDatabase
import com.owner.basemodule.room.entities.User
import io.reactivex.Observable

interface ILocalPlaceOrderDataSource : ILocalDataSource {

    fun getAllSupplier(): Observable<MutableList<User>>
}

class LocalPlaceOrderDataSourceImpl(
    private val database: AppDatabase
) : ILocalPlaceOrderDataSource {

    override fun getAllSupplier(): Observable<MutableList<User>> {

        return database.userDao().getUserOfRole("供应商")
    }

}