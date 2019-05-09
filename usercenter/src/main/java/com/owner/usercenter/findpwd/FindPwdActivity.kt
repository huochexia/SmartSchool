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

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.jakewharton.rxbinding3.view.clicks
import com.owner.basemodule.base.error.Errors
import com.owner.basemodule.base.view.activity.BaseActivity
import com.owner.usercenter.R
import com.owner.usercenter.databinding.ActivityResetPwdBinding
import com.owner.usercenter.mvi.MVIActivity
import com.owner.usercenter.resetpwd.ResetPwdActivity
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_forget_pwd.*
import kotlinx.coroutines.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import kotlin.coroutines.CoroutineContext

/**
 *
 * Created by Liuyong on 2019-04-21.It's smartschool
 *@description:
 */

class FindPwdActivity :
    MVIActivity<ActivityResetPwdBinding, IntentFindPwd, ViewStateFindPwd>(),
    CoroutineScope {

    lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override val layoutId: Int
        get() = R.layout.activity_forget_pwd

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(findPwdModule)
    }

    private val findPwdIntentPublisher =
        PublishSubject.create<IntentFindPwd>()

    val mViewModel: ViewModelFindPwd by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun initView() {
        super.initView()

        mViewModel.processIntents(intents())

        mViewModel.states()
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe(this::render)

        initEvent()
    }

    private fun initEvent() {
        btnGetVerifyCode.clicks()
            .doOnNext {
                //在这里启动倒计时的协程
                launch(coroutineContext) {
                    try {
                        btnGetVerifyCode.isEnabled = false
                        countDown()
                    } catch (e: Throwable) {

                    } finally {
                        btnGetVerifyCode.text = "重新获取"
                        btnGetVerifyCode.isEnabled = true
                    }

                }
            }
            .map {
                IntentFindPwd.IntentClickGetVerifyCode(tvUserActor.text.toString())

            }
            .autoDisposable(scopeProvider)
            .subscribe(findPwdIntentPublisher)

        btnNext.clicks()
            .map {
                IntentFindPwd.IntentClickNextBtn(
                    tvVerifyCode.text.toString()
                )
            }
            .autoDisposable(scopeProvider)
            .subscribe(findPwdIntentPublisher)

    }

    override fun intents(): Observable<IntentFindPwd> {
        return Observable.mergeArray(findPwdIntentPublisher)
    }

    override fun render(state: ViewStateFindPwd) {
        state.error?.apply {
            when (this) {
                is Errors.SimpleMessageError -> {
                    Toast.makeText(this@FindPwdActivity, simpleMessage, Toast.LENGTH_SHORT).show()
                }
                is Errors.BmobError -> {
                    Toast.makeText(this@FindPwdActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        state.uiEvent?.apply {
            when (this) {
                is ViewStateFindPwd.FindPwdEvent.sendSmsCode -> {


                }
                is ViewStateFindPwd.FindPwdEvent.jumpReset -> {
                    val intent = Intent(this@FindPwdActivity, ResetPwdActivity::class.java)
                    intent.putExtra("smsCode", smsCode)
                    startActivity(intent)
                }
            }
        }
    }

    /**
     * 倒计时函数
     */
    private suspend fun countDown() {
        for (counter in 59 downTo 0) {
            btnGetVerifyCode.text = counter.toString() + "秒"
            delay(1000)
        }
    }
}