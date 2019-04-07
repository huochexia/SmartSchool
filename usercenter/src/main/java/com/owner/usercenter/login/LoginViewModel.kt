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

import androidx.lifecycle.MutableLiveData
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 * 通过七步曲，将Intent转换成ViewState。
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */
class LoginViewModel(
    private val processorHolder: LoginActionProcessHolder //第四步，获取网络数据生成Result
) : BaseViewModel<LoginIntent, LoginViewState>() {

    //控件的可视性
    var isVisiblity = MutableLiveData<Boolean>()
    var isAutoLogin = MutableLiveData<Boolean>()

    //接收Intent的桥架对象
    private val intentsSubject: PublishSubject<LoginIntent> = PublishSubject.create()
    //最后状态对象
    private val statesObservable: Observable<LoginViewState> = compose()

    /*
      第一步：通过这个方法将Intent数据传递给桥架对象
     */
    override fun processIntents(intent: Observable<LoginIntent>) {
        intent.autoDisposable(this).subscribe(intentsSubject)
    }

    /*
       第二步：过滤最初的Intent数据流
     */
    private val intentFilter: ObservableTransformer<LoginIntent, LoginIntent>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.merge(
                    shared.ofType(LoginIntent.InitialIntent::class.java).take(1),
                    shared.filter { it !is LoginIntent.InitialIntent }
                )
            }

        }

    /*
        第三步：将Intent转换成Action
     */
    private fun actionFromIntent(intent: LoginIntent): LoginAction {
        return when (intent) {
            is LoginIntent.InitialIntent -> LoginAction.InitialUiAction
            is LoginIntent.SetAutoLoginIntent -> LoginAction.SetAutoLoginAction(intent.isAutoLogin)
            is LoginIntent.LoginClicksIntent -> LoginAction.ClickLoginAction(intent.username, intent.password)
            is LoginIntent.FindPassWordIntent -> LoginAction.FindPassWordAction
        }
    }


    /*
     * 第五步：定义利用Result更新状态的方法。主要是使用When操作符，针对不同情况分别处理。
     */
    companion object {
        private val reducer = BiFunction { previousState: LoginViewState, result: LoginResult ->
            when (result) {
                /*
                    点击登录按钮时状态
                 */
                is LoginResult.ClickLoginResult -> when (result) {
                    is LoginResult.ClickLoginResult.Success -> {
                        previousState.copy(
                            isLoading = false,
                            errors = null,
                            uiEvents = LoginViewState.LoginUiEvent.JumpMain(result.user)
                        )
                    }
                    is LoginResult.ClickLoginResult.Failure -> previousState.copy(
                        isLoading = false,
                        errors = result.error,
                        uiEvents = null
                    )
                    is LoginResult.ClickLoginResult.InFlight -> previousState.copy(isLoading = true)
                }
                /*
                设置自动登录选择框状态
                 */
                is LoginResult.SetAutoLoginInfoResult ->
                    previousState.copy(
                        uiEvents = LoginViewState.LoginUiEvent.SetAutoLoginInfo(
                            isAutoLogin = result.isAutoLogin
                        )
                    )

                /*
                进行自动登录时状态
                 */
                is LoginResult.AutoLoginInfoResult -> when (result) {
                    is LoginResult.AutoLoginInfoResult.Success -> {
                        previousState.copy(
                            isLoading = false,
                            uiEvents = LoginViewState.LoginUiEvent.TryAutoLogin(
                                loginEntity = result.user,
                                autoLogin = result.autoLogin
                            )
                        )
                    }
                    is LoginResult.AutoLoginInfoResult.Failure -> previousState.copy(
                        isLoading = false,
                        errors = result.error
                    )
                    is LoginResult.AutoLoginInfoResult.NoUserData -> {
                        previousState.copy(isLoading = false)
                    }
                    is LoginResult.AutoLoginInfoResult.InFlight -> previousState.copy(
                        isLoading = true
                    )
                }
                /*
                   执行找回密码
                 */
                is LoginResult.FindPassWordResult -> previousState.copy(
                    uiEvents = LoginViewState.LoginUiEvent.JumpFindPassWord
                )
            }
        }
    }

    /*
      第六步： 依次组合各阶段的数据流，得到最后状态数据
     */
    override fun compose(): Observable<LoginViewState> {
        return intentsSubject
            .compose(intentFilter)
            .map { actionFromIntent(it) }
            .compose(processorHolder.actionProcessor)
            .scan(LoginViewState.idle(), reducer)
            .replay(1)
            .autoConnect(0)

    }

    /*
     第六步： 外界通过这个方法获取最终状态，
    */
    override fun states(): Observable<LoginViewState> = statesObservable

}