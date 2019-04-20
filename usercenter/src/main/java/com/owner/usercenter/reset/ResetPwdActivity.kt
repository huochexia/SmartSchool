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
package com.owner.usercenter.reset

import android.widget.Toast
import com.jakewharton.rxbinding3.view.clicks
import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.usercenter.R
import com.owner.usercenter.databinding.ActivityResetPwdBinding
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_reset_pwd.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 *
 * Created by Liuyong on 2019-04-12.It's smartschool
 *@description:
 */
class ResetPwdActivity : BaseActivity<ActivityResetPwdBinding, ResetIntent, ResetViewState>() {

    override val layoutId: Int
        get() = R.layout.activity_reset_pwd

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(resetModule)
    }

    private val resetIntentPublisher = PublishSubject.create<ResetIntent>()

    val viewModel: ResetViewModel by instance()

    override fun initView() {
        initEvent()

        viewModel.processIntents(intents())

        viewModel.states()
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                render(it)
            }, {
                Toast.makeText(this@ResetPwdActivity, it.message, Toast.LENGTH_SHORT).show()
            })
    }

    fun initEvent() {
        btnReset.clicks()
            .map {
                ResetIntent.ClickResetIntent(
                    oldPassword = tvOldPassword.text.toString(),
                    newPassword = tvNewPassword.text.toString(),
                    againPassword = tvAgainPassword.text.toString()
                )
            }
            .autoDisposable(scopeProvider)
            .subscribe(resetIntentPublisher)
    }

    override fun intents(): Observable<ResetIntent> {
        return Observable.mergeArray(resetIntentPublisher)
    }

    override fun render(state: ResetViewState) {
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

        state.uiEvent?.apply {
            when (this) {
                is ResetViewState.ResetUiEvent.Success -> {

                    Toast.makeText(this@ResetPwdActivity, "更改成功！", Toast.LENGTH_SHORT).show()
                }

            }
        }

    }
}