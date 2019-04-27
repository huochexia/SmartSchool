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

import android.os.Bundle
import android.widget.Toast
import com.jakewharton.rxbinding3.view.clicks
import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.usercenter.R
import com.owner.usercenter.databinding.ActivityResetPwdBinding
import com.owner.usercenter.login.LoginActivity
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_reset_pwd.*
import org.jetbrains.anko.startActivity
import org.kodein.di.Kodein
import org.kodein.di.generic.instance


/**
 *
 * Created by Liuyong on 2019-04-25.It's smartschool
 *@description:
 */
class ResetPwdActivity : BaseActivity<ActivityResetPwdBinding, ResetPwdIntent, ResetPwdViewState>() {
    override val layoutId: Int
        get() = R.layout.activity_reset_pwd

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(resetPwdModule)
    }

    val mViewModel: ResetPwdViewModle by instance()
    lateinit var smsCode: String
    private val intentPublisher = PublishSubject.create<ResetPwdIntent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
    override fun initView() {
        smsCode = intent.getStringExtra("smsCode")

        mViewModel.processIntents(intents())

        mViewModel.states()
            .autoDisposable(scopeProvider)
            .subscribe(::render)
        initEvent()
    }

    private fun initEvent() {
        btnReset.clicks()
            .map {
                ResetPwdIntent(tvNewPassword.text.toString(), tvAgainPassword.text.toString(), smsCode)
            }
            .autoDisposable(scopeProvider)
            .subscribe(intentPublisher)
    }

    override fun intents(): Observable<ResetPwdIntent> {
        return intentPublisher
    }

    override fun render(state: ResetPwdViewState) {
        state.error?.apply {
            when (this) {
                is Errors.SimpleMessageError -> {
                    Toast.makeText(this@ResetPwdActivity, simpleMessage, Toast.LENGTH_SHORT).show()
                }
                is Errors.BmobError -> {
                    Toast.makeText(this@ResetPwdActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
        state.uiEvent?.let {
            when (it) {
                is ResetPwdViewState.ResetPwdUiEvent.JumpLogin -> {
                    startActivity<LoginActivity>()
                    finish()
                }
            }
        }
    }
}