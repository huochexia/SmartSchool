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
package com.owner.usercenter.findpwd

import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 *
 * Created by Liuyong on 2019-04-21.It's smartschool
 *@description:
 */
class ViewModelFindPwd(
    private val processor: FindPwdActionProcessHolder
) : BaseViewModel<IntentFindPwd, ViewStateFindPwd>() {

    private val intentsPublisher = PublishSubject.create<IntentFindPwd>()

    private val stateObservable: Observable<ViewStateFindPwd> = compose()

    override fun compose(): Observable<ViewStateFindPwd> {
        return intentsPublisher
            .map { actionFromIntent(it) }
            .compose(processor.actionProcessor)
            .scan(ViewStateFindPwd.idle(), reducer)
            .publish()
            .autoConnect(0)
    }

    override fun processIntents(intent: Observable<IntentFindPwd>) =
        intent.autoDisposable(this).subscribe(intentsPublisher)

    override fun states(): Observable<ViewStateFindPwd> = stateObservable

    /**
     * 这个是使用在map操作符当中的函数，将Intent变成对应的Action
     */
    private fun actionFromIntent(intent: IntentFindPwd): ActionFindPwd {
        return when (intent) {
            is IntentFindPwd.IntentClickGetVerifyCode -> ActionFindPwd.ActionClickGetVerifyCode(intent.mobilephone)
            is IntentFindPwd.IntentClickNextBtn -> ActionFindPwd.ActionClickNextBtn( intent.smsCode)
        }
    }

    companion object {
        private val reducer = BiFunction { previous: ViewStateFindPwd, result: ResultFindPwd ->
            when (result) {
                is ResultFindPwd.GetVerifyCode -> when (result) {
                    is ResultFindPwd.GetVerifyCode.Success -> previous.copy(
                        error = null,
                        uiEvent = ViewStateFindPwd.FindPwdEvent.sendSmsCode(result.smsCode)
                    )
                    is ResultFindPwd.GetVerifyCode.Failure -> previous.copy(
                        error = result.error
                    )


                }
                is ResultFindPwd.NextBtn -> when (result) {
                    is ResultFindPwd.NextBtn.Success -> previous.copy(
                        error = null,
                        uiEvent = ViewStateFindPwd.FindPwdEvent.jumpReset(result.smsCode)
                    )
                    is ResultFindPwd.NextBtn.Failure -> previous.copy(
                        error = result.error
                    )

                }
            }
        }
    }

}