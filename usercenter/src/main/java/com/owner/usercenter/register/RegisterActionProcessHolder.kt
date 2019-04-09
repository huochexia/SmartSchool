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
package com.owner.usercenter.register

import com.owner.basemodule.base.error.Errors
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 *
 * Created by Liuyong on 2019-04-07.It's smartschool
 *@description:
 */
class RegisterActionProcessHolder(
    private val repository: RegisterDataSourceRepository
) {
    private val initialUIActionTransformer =
        ObservableTransformer<RegisterAction.InitialRegisterAction, RegisterResult.InitialRegisterResult> { action ->
            action.flatMap {
                Observable.just(RegisterResult.InitialRegisterResult)
            }
        }
    private val clickRegisterActionTransformer =
        ObservableTransformer<RegisterAction.ClickRegisterAction, RegisterResult.ClickRegisterResult> { action ->

            action.flatMap { it ->
                val (username, mobilephone) = it
                when (username.isNullOrEmpty() || mobilephone.isNullOrEmpty()) {
                    true -> onRegisterParamEmptyResult()
                    false -> repository.register(it.username!!, it.mobilephone!!)
                        .toObservable()
                        .flatMap { either ->
                            either.fold({
                                onRegisterFailureResult(it)
                            }, {
                                onRegisterSuccessResult()
                            })
                        }
                }
            }

        }

    /**
     * 自动用户名或密码为空时发出的结果
     */
    private fun onRegisterParamEmptyResult(): Observable<RegisterResult.ClickRegisterResult> {
        return Observable.just(Errors.SimpleMessageError("用户名或手机不能为空！"))
            .map(RegisterResult.ClickRegisterResult::Failure)
    }

    /**
     * 注册成功发射成功结果对象
     */
    private fun onRegisterSuccessResult(): Observable<RegisterResult.ClickRegisterResult> {
        return Observable.just(RegisterResult.ClickRegisterResult.Success)
    }

    /**
     * 注册失败，发射失败结果
     */
    private fun onRegisterFailureResult(error: Throwable): Observable<RegisterResult.ClickRegisterResult> {
        return Observable.just(RegisterResult.ClickRegisterResult.Failure(error))
    }

    /**
     * 分类合并所有转换过程
     */
    internal val actionProcessor =
        ObservableTransformer<RegisterAction, RegisterResult> { action ->
            action.publish { shared ->
                Observable.merge(
                    shared.ofType(RegisterAction.InitialRegisterAction::class.java).compose(initialUIActionTransformer),
                    shared.ofType(RegisterAction.ClickRegisterAction::class.java).compose(clickRegisterActionTransformer)
                )
            }

        }

}