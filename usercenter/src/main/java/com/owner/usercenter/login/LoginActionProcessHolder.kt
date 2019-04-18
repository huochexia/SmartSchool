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
package com.owner.usercenter.login

import arrow.core.Either
import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.ext.reactivex.execute
import com.owner.usercenter.http.entities.LoginResp
import com.owner.usercenter.http.entities.LoginUser
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction

/**
 * 处理从Action到Result过程逻辑
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */
class LoginActionProcessHolder(
    private val repository: LoginDataSourceRepository
) {
    //初始化过程
    private val initialActionTransformer =
        ObservableTransformer<LoginAction.InitialAction, LoginResult.AutoLoginResult> { action ->
            Observable
                .zip(repository.prefsUser().toObservable(),
                    repository.prefsAutoLogin().toObservable(),
                    BiFunction { either: Either<Errors, LoginUser>, autoLogin: Boolean ->
                        either.fold({
                            LoginResult.AutoLoginResult.NoUserData
                        }, { user ->
                            LoginResult.AutoLoginResult.Success(user, autoLogin)
                        })
                    })
                .onErrorReturn(LoginResult.AutoLoginResult::Failure)
                .execute()
                .startWith(LoginResult.AutoLoginResult.InFlight)
        }


    //设置是否自动登录
    private val setAutoLoginActionTransformer =
        ObservableTransformer<LoginAction.SetAutoLoginAction, LoginResult.SetAutoLoginInfoResult> { action ->
            action.flatMap {
                onAutoLoginResult(it)
            }
        }
    //找回密码的Action转换Result
    private val findPassWordActionTransformer =
        ObservableTransformer<LoginAction.FindPassWordAction, LoginResult.FindPassWordResult> { action ->
            action.flatMap { Observable.just(LoginResult.FindPassWordResult) }
        }
    //
    private val loginClickActionTransformer =
        ObservableTransformer<LoginAction.ClickLoginAction, LoginResult.ClickLoginResult> { actions ->
            actions
                .flatMap { o ->
                    val (username, password) = o
                    when (username.isNullOrEmpty() || password.isNullOrEmpty()) {
                        true -> onLoginParamEmptyResult()
                        false -> repository.login(username, password)
                            .toObservable()
                            .flatMap {
                                it.fold(::onLoginFailureResult, ::onLoginSuccessResult)
                            }
                    }

                }
                .onErrorReturn(LoginResult.ClickLoginResult::Failure)
                .execute()
                .startWith(LoginResult.ClickLoginResult.InFlight)
        }


    /**
     * 自动用户名或密码为空时发出的结果
     */
    private fun onLoginParamEmptyResult(): Observable<LoginResult.ClickLoginResult> {
        return Observable.just(Errors.SimpleMessageError("用户名或密码不能为空！"))
            .map(LoginResult.ClickLoginResult::Failure)
    }

    /**
     * 设置自动登录状态信息
     */
    private fun onAutoLoginResult(action: LoginAction.SetAutoLoginAction): Observable<LoginResult.SetAutoLoginInfoResult> {
        return Observable.just(LoginResult.SetAutoLoginInfoResult(action.isAutoLogin))
    }

    /**
     * 点击登录失败时发出的结果
     */
    private fun onLoginFailureResult(error: Errors): Observable<LoginResult.ClickLoginResult> {
        return Observable.just(LoginResult.ClickLoginResult.Failure(error))
    }

    /**
     * 点击登录成功时发出的结果
     */
    private fun onLoginSuccessResult(loginUser: LoginResp): Observable<LoginResult.ClickLoginResult> {
        return Observable.just(LoginResult.ClickLoginResult.Success(loginUser))
    }

    /**
     * 对各个情况转换流进行合并
     */
    internal val actionProcessor =
        ObservableTransformer<LoginAction, LoginResult> { actions ->
            actions.publish { shared ->
                Observable.mergeArray(
                    shared.ofType(LoginAction.InitialAction::class.java).compose(initialActionTransformer),
                    shared.ofType(LoginAction.SetAutoLoginAction::class.java).compose(setAutoLoginActionTransformer),
                    shared.ofType(LoginAction.ClickLoginAction::class.java).compose(loginClickActionTransformer),
                    shared.ofType(LoginAction.FindPassWordAction::class.java).compose(findPassWordActionTransformer)
                )
            }
        }
}