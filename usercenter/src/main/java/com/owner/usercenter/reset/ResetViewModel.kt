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

import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 *
 * Created by Liuyong on 2019-04-12.It's smartschool
 *@description:
 */
class ResetViewModel(
    private val actionProcessHolder: ResetActionProcessHolder
) : BaseViewModel<ResetIntent, ResetViewState>() {

    private val intentPublish = PublishSubject.create<ResetIntent>()

    private val stateObservable: Observable<ResetViewState> = compose()

    override fun compose(): Observable<ResetViewState> {
        return intentPublish.map { actionFromIntent(it) }
            .compose(actionProcessHolder.actionProcessor)
            .scan(ResetViewState.idle(), reducer)
            .replay(1)
            .autoConnect(0)
    }

    override fun processIntents(intent: Observable<ResetIntent>) {
        intent.autoDisposable(this)
            .subscribe(intentPublish)
    }

    override fun states(): Observable<ResetViewState> = stateObservable

    private fun actionFromIntent(intent: ResetIntent): ResetAction {
        return when (intent) {
            is ResetIntent.ClickResetIntent -> ResetAction.ClickResetAction(
                intent.oldPassword,
                intent.newPassword, intent.againPassword
            )

        }
    }

    companion object {
        private val reducer = BiFunction { previous: ResetViewState, result: ResetResult ->

            when (result) {
                is ResetResult.ClickResetResult -> when (result) {
                    is ResetResult.ClickResetResult.Failure -> previous.copy(
                        error = result.error,
                        uiEvent = null
                    )
                    is ResetResult.ClickResetResult.Success -> previous.copy(
                        error = null,
                        uiEvent = ResetViewState.ResetUiEvent.Success
                    )
                }
            }
        }
    }
}