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

import com.owner.basemodule.base.error.Errors
import com.owner.usercenter.http.entities.ResetPwdResp
import com.owner.usercenter.util.PrefsHelper
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import java.lang.IllegalArgumentException

/**
 *
 * Created by Liuyong on 2019-04-12.It's smartschool
 *@description:
 */
class ResetActionProcessHolder(
    private val repository: ResetDataSourceRepository,
    private val prefs:PrefsHelper
) {

    private val clickResetPwdTransformer =
        ObservableTransformer<ResetAction.ClickResetAction, ResetResult.ClickResetResult> { action ->

            action.flatMap {
                val (oldPassword,newPassword, againPassword) = it
                when (newPassword != againPassword) {
                    true -> onResetParamErrorResult()
                    false -> {repository.resetPwd(prefs.sessionToken,prefs.objectId,oldPassword,newPassword)
                                .toObservable()
                                .flatMap { either ->
                                    either.fold({ error ->
                                        onResetFailureResult(error)
                                    }, { resp ->
                                        onResetSuccessResult(resp)
                                    })
                                }
                    }
                }
            }

        }

    private fun onResetParamErrorResult(): Observable<ResetResult.ClickResetResult> {
        return Observable.just(Errors.SimpleMessageError("新旧密码不一致！"))
            .map(ResetResult.ClickResetResult::Failure)
    }

    private fun onResetFailureResult(error: Throwable): Observable<ResetResult.ClickResetResult> {
        return Observable.just(ResetResult.ClickResetResult.Failure(error))
    }

    private fun onResetSuccessResult(respone: ResetPwdResp): Observable<ResetResult.ClickResetResult> {
        return Observable.just(respone)
            .filter { it.msg == "ok" }
            .map {
                ResetResult.ClickResetResult.Success
            }
    }

    internal val actionProcessor = ObservableTransformer<ResetAction, ResetResult> { action ->

        action.publish { share ->
            Observable.mergeArray(
                share.ofType(ResetAction.ClickResetAction::class.java).compose(clickResetPwdTransformer),
                share.filter {all->
                    all !is ResetAction.ClickResetAction
                }.flatMap {
                    Observable.error<ResetResult>(IllegalArgumentException("Unknown Action type!"))
                }
            )
        }
    }

}