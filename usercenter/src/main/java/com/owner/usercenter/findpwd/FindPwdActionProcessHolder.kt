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

import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.ext.reactivex.execute
import com.owner.usercenter.http.entities.RequestCodeResp
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 *
 * Created by Liuyong on 2019-04-21.It's smartschool
 *@description:
 */
class FindPwdActionProcessHolder(
    private val repository: FindPwdRepositoryDataSource
) {
    private val clickGetVerifyCodeActionProcessor =
        ObservableTransformer<ActionFindPwd.ActionClickGetVerifyCode, ResultFindPwd.GetVerifyCode> { action ->

            action.flatMap {
                val mobilephone = it.mobilephone
                when (mobilephone.isNullOrEmpty() || mobilephone.length < 11) {
                    true -> onActionParamError()
                    false -> repository.getSmsCode(it.mobilephone)
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .flatMap { either ->
                            either.fold({ error ->
                                onFindPwdActionSuccessFailure(error)
                            }, {
                                onFindPwdActionSuccess(it)
                            })
                        }
                }

            }
                .onErrorReturn(ResultFindPwd.GetVerifyCode::Failure)
                .observeOn(AndroidSchedulers.mainThread())

        }

    private fun onFindPwdActionSuccess(it: RequestCodeResp) =
        Observable.just(ResultFindPwd.GetVerifyCode.Success(it.smsId!!))

    private fun onFindPwdActionSuccessFailure(error: Errors) =
        Observable.just(ResultFindPwd.GetVerifyCode.Failure(error))

    private fun onActionParamError() = Observable.just(Errors.SimpleMessageError("手机号码不正确！"))
        .map(ResultFindPwd.GetVerifyCode::Failure)

    private val clickNextActionTransformer =
        ObservableTransformer<ActionFindPwd.ActionClickNextBtn, ResultFindPwd.NextBtn> { action ->
            action.flatMap {
                when (it.smsCode.isNullOrEmpty()) {
                    true -> Observable.just(Errors.SimpleMessageError("没有验证码"))
                        .map { ResultFindPwd.NextBtn.Failure(it) }
                    false -> Observable.just(ResultFindPwd.NextBtn.Success(it.smsCode))
                }
            }
                .onErrorReturn(ResultFindPwd.NextBtn::Failure)
                .execute()

        }

    internal val actionProcessor =
        ObservableTransformer<ActionFindPwd, ResultFindPwd> { actions ->
            actions.publish { share ->
                Observable.merge(
                    share.ofType(ActionFindPwd.ActionClickGetVerifyCode::class.java)
                        .compose(clickGetVerifyCodeActionProcessor),
                    share.ofType(ActionFindPwd.ActionClickNextBtn::class.java).compose(clickNextActionTransformer)
                )
            }
        }

}