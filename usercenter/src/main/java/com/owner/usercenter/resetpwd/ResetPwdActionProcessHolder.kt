/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.owner.usercenter.resetpwd

import com.owner.basemodule.base.error.Errors
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 *
 * Created by Liuyong on 2019-04-23.It's smartschool
 *@description:
 */
class ResetPwdActionProcessHolder(
    private val repository: ResetPwdRepository
) {

    internal val clickResetPwdTransformer =
        ObservableTransformer<ResetPwdAction, ResetPwdResult> { action ->

            action.flatMap { o ->
                val (newPassword, againPassword, smsCode) = o
                when (newPassword != againPassword) {
                    true -> onInputParamError()
                    false -> repository.resetPassword(newPassword, smsCode)
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .flatMap { either ->
                            either.fold({ error ->
                                Observable.just(ResetPwdResult.Failure(error))
                            }, {
                                Observable.just(ResetPwdResult.Success)
                            })

                        }
                }

            }
                .onErrorReturn(ResetPwdResult::Failure)
                .observeOn(AndroidSchedulers.mainThread())

        }

    fun onInputParamError(): Observable<ResetPwdResult> {
        return Observable.just(Errors.SimpleMessageError("密码不一致！"))
            .map {
                ResetPwdResult.Failure(it)
            }
    }


}