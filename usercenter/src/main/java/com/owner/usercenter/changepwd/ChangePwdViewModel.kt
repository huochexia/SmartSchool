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
package com.owner.usercenter.changepwd

import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.usercenter.mvi.MVIViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 *
 * Created by Liuyong on 2019-04-12.It's smartschool
 *@description:
 */
class ChangePwdViewModel(
    private val actionProcessHolder: ChangePwdActionProcessHolder
) : MVIViewModel<ChangePwdIntent, ChangePwdViewState>() {

    private val intentPublish = PublishSubject.create<ChangePwdIntent>()

    private val stateObservable: Observable<ChangePwdViewState> = compose()

    override fun compose(): Observable<ChangePwdViewState> {
        return intentPublish.map { actionFromIntent(it) }
            .compose(actionProcessHolder.actionProcessor)
            .scan(ChangePwdViewState.idle(), reducer)
            .replay(1)
            .autoConnect(0)
    }

    override fun processIntents(intent: Observable<ChangePwdIntent>) {
        intent.autoDisposable(this)
            .subscribe(intentPublish)
    }

    override fun states(): Observable<ChangePwdViewState> = stateObservable

    private fun actionFromIntent(intent: ChangePwdIntent): ChangePwdAction {
        return when (intent) {
            is ChangePwdIntent.ClickResetIntent -> ChangePwdAction.ClickResetAction(
                intent.oldPassword,
                intent.newPassword, intent.againPassword
            )

        }
    }

    companion object {
        private val reducer = BiFunction { previous: ChangePwdViewState, result: ChangePwdResult ->

            when (result) {
                is ChangePwdResult.ClickResetResult -> when (result) {
                    is ChangePwdResult.ClickResetResult.Failure -> previous.copy(
                        error = result.error,
                        uiEvent = null
                    )
                    is ChangePwdResult.ClickResetResult.Success -> previous.copy(
                        error = null,
                        uiEvent = ChangePwdViewState.ResetUiEvent.Success
                    )
                }
            }
        }
    }
}