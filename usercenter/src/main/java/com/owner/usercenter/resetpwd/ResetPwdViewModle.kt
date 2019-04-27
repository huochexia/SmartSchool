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

import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 *
 * Created by Liuyong on 2019-04-23.It's smartschool
 *@description:
 */
class ResetPwdViewModle(
    private val processor: ResetPwdActionProcessHolder
) : BaseViewModel<ResetPwdIntent, ResetPwdViewState>() {

    private val intentPublisher =
        PublishSubject.create<ResetPwdIntent>()

    private val stateObservable: Observable<ResetPwdViewState> = compose()
    override fun compose(): Observable<ResetPwdViewState> {
        return intentPublisher.map { actionFromIntent(it) }
            .compose(processor.clickResetPwdTransformer)
            .scan(ResetPwdViewState.idle(), reducer)
            .publish()
            .autoConnect(0)
    }

    override fun processIntents(intent: Observable<ResetPwdIntent>) {
        intent.autoDisposable(this).subscribe(intentPublisher)
    }

    override fun states(): Observable<ResetPwdViewState> = stateObservable

    private fun actionFromIntent(intent: ResetPwdIntent): ResetPwdAction {
        return ResetPwdAction(intent.newPassword, intent.againPassword, intent.smsCode)

    }

    companion object {
        val reducer = BiFunction { previous: ResetPwdViewState, result: ResetPwdResult ->
            when (result) {
                is ResetPwdResult.Success -> previous.copy(
                    error = null,
                    uiEvent = ResetPwdViewState.ResetPwdUiEvent.JumpLogin
                )
                is ResetPwdResult.Failure -> previous.copy(
                    error = result.error
                )

            }
        }
    }
}