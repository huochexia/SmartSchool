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

import androidx.lifecycle.MutableLiveData
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.usercenter.mvi.MVIViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject


/**
 *
 * Created by Liuyong on 2019-04-09.It's smartschool
 *@description:
 */
class RegisterViewModel(
    private val actionProcessHolder: RegisterActionProcessHolder
) : MVIViewModel<RegisterIntent, RegisterViewState>() {

    var isVisiblity = MutableLiveData<Boolean>()

    private val intentSubject = PublishSubject.create<RegisterIntent>()
    private val stateObservable: Observable<RegisterViewState> = compose()

    private fun actionFromIntent(intent: RegisterIntent): RegisterAction =
        when (intent) {
            is RegisterIntent.InitialRegisterIntent -> RegisterAction.InitialRegisterAction
            is RegisterIntent.ClickRegisterIntent -> RegisterAction.ClickRegisterAction(
                intent.username,
                intent.mobilephone
            )
        }


    override fun processIntents(intent: Observable<RegisterIntent>) {
        //从View得到Intent
        intent.autoDisposable(this)
            .subscribe(intentSubject)
    }

    override fun states(): Observable<RegisterViewState> {
        return stateObservable
    }

    companion object {
        private val reducer = BiFunction { previousState: RegisterViewState, result: RegisterResult ->
            when (result) {
                is RegisterResult.InitialRegisterResult ->
                    previousState.copy(
                        isLoading = false,
                        errors = null,
                        uiEvent =RegisterViewState.RegisterUIEvent.initialUI
                    )
                is RegisterResult.ClickRegisterResult -> when (result) {
                    is RegisterResult.ClickRegisterResult.Success -> previousState.copy(
                        isLoading = false,
                        errors = null,
                        uiEvent = RegisterViewState.RegisterUIEvent.showDialogBox
                    )
                    is RegisterResult.ClickRegisterResult.Failure -> previousState.copy(
                        isLoading = true,
                        errors = result.error,
                        uiEvent = null
                    )
                }
            }
        }
    }

    override fun compose(): Observable<RegisterViewState> {
        return intentSubject.map { actionFromIntent(it) }
            .compose(actionProcessHolder.actionProcessor)
            .scan(RegisterViewState.idle(), reducer)
            .publish()
            .autoConnect(0)
    }
}