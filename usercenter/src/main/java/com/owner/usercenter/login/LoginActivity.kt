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

import android.widget.Toast
import com.jakewharton.rxbinding3.view.clicks
import com.owner.basemodule.base.error.Errors
import com.owner.usercenter.R
import com.owner.usercenter.databinding.ActivityLoginBinding
import com.owner.usercenter.findpwd.FindPwdActivity
import com.owner.usercenter.mvi.MVIActivity
import com.owner.usercenter.util.PrefsHelper
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 *
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */
class LoginActivity : MVIActivity<ActivityLoginBinding, LoginIntent, LoginViewState>() {


    override val layoutId: Int
        get() = R.layout.activity_login

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(loginKodeinModule)
    }
    //定义接收数据流的桥架，数据流由RxBinding定义的控件方法产生。
    private val loginIntentPublisher =
        PublishSubject.create<LoginIntent>()

    //定义ViewModel
    val viewModel: LoginViewModel by instance()

    val prefs: PrefsHelper by instance()

    //初始化视图
    override fun initView() {
        //从本地获取自动登录状态，初始化界面
        viewModel.isAutoLogin.value = prefs.autoLogin

        //发送Intent
        viewModel.processIntents(intents())
        //接收最终状态
        viewModel.states()
            .autoDisposable(scopeProvider)
            .subscribe(this::render)

        initEvent()
    }

    /**
     * 初始化未来的Event
     */
    private fun initEvent() {

        //登录按钮点击事件，转换成Intent
        btnLogin.clicks()
            .map {
                LoginIntent.LoginClicksIntent(
                    mobilePhone = tvUserActor.text.toString(),
                    password = tvPassword.text.toString()
                )
            }
            .autoDisposable(scopeProvider)
            .subscribe(loginIntentPublisher)

        //设置自动登录事件
        ckAutoLogin.clicks()
            .flatMap {
                //isAutoLogin已经进行了双向绑定，所以这里得到的是新值
                Observable.just(LoginIntent.SetAutoLoginIntent(viewModel.isAutoLogin.value))
            }
            .autoDisposable(scopeProvider)
            .subscribe(loginIntentPublisher)
        //忘记密码
        tv_forget_password.clicks()
            .map {
                LoginIntent.FindPassWordIntent
            }
            .autoDisposable(scopeProvider)
            .subscribe(loginIntentPublisher)
    }

    override fun intents(): Observable<LoginIntent> = Observable.mergeArray(
        loginIntentPublisher
    )


    override fun render(state: LoginViewState) {
        //对错误状态的渲染
        state.errors?.apply {
            when (this) {
                is Errors.SimpleMessageError -> {
                    Toast.makeText(this@LoginActivity, simpleMessage, Toast.LENGTH_SHORT).show()
                }
                is Errors.BmobError -> {
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }

            }
        }
        //对进程控件的渲染
        viewModel.isVisiblity.value = state.isLoading
        //渲染登录事件
        when (state.uiEvents) {

            is LoginViewState.LoginUiEvent.JumpMain -> {
                Toast.makeText(this@LoginActivity, "登录成功！！", Toast.LENGTH_SHORT).show()

            }
            is LoginViewState.LoginUiEvent.SetAutoLoginInfo -> {
                prefs.autoLogin = state.uiEvents.isAutoLogin!!.apply {
                    if (!this) prefs.password = ""
                }

            }
            //如果是自动登录，则利用得到的用户名和密码，发出点击登录意图
            is LoginViewState.LoginUiEvent.TryAutoLogin -> {
                val username = state.uiEvents.loginEntity.mobilePhoneNumber
                val password = state.uiEvents.loginEntity.password
                tvUserActor.setText(username.toCharArray(), 0, username.length)
                tvPassword.setText(password.toCharArray(), 0, password.length)
                if (state.uiEvents.autoLogin) {
                    loginIntentPublisher.onNext(LoginIntent.LoginClicksIntent(username, password))
                }
            }
            is LoginViewState.LoginUiEvent.JumpFindPassWord -> {
                startActivity<FindPwdActivity>()
            }
        }
    }
}