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

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.jakewharton.rxbinding3.view.clicks
import com.owner.basemodule.base.error.Errors
import com.owner.usercenter.R
import com.owner.usercenter.databinding.ActivityRegisterBinding
import com.owner.usercenter.mvi.MVIActivity
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_register.*
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 *
 * Created by Liuyong on 2019-04-07.It's smartschool
 *@description:
 */
@Route(path = "/usercenter/register")
class RegisterActivity : MVIActivity<ActivityRegisterBinding, RegisterIntent, RegisterViewState>() {

    override val layoutId: Int
        get() = R.layout.activity_register

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
        import(registerKodeinModule)
    }

    private val registerIntentPublish = PublishSubject.create<RegisterIntent>()

    val viewModel by instance<RegisterViewModel>()
    //默认值
    var role = ""
    var district = 0
    var categoryCode = "-1"

    override fun initView() {

        //准备要发射的数据
        initEvent()
        //连接数据通道开端
        viewModel.processIntents(intents())
        //连接数据通道末端
        viewModel.states()
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(scopeProvider)
            .subscribe({
                render(it)
            }, {
                println("errros::" + it.message)
            })

    }

    override fun intents(): Observable<RegisterIntent> {
        return Observable.mergeArray(registerIntentPublish)
    }

    override fun render(state: RegisterViewState) {
        state.errors?.apply {
            when (this) {
                is Errors.BmobError -> {
                    Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
                is Errors.SimpleMessageError -> {
                    Toast.makeText(this@RegisterActivity, simpleMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
        when (state.uiEvent) {

            is RegisterViewState.RegisterUIEvent.showDialogBox -> {
                AlertDialog.Builder(this@RegisterActivity)
                    .setMessage("是否继续注册新用户！")
                    .setPositiveButton("是") { _, _ ->
                        tvUserActor.setText("")
                        tvNewUsername.setText("")
                    }
                    .setNegativeButton("否") { _, _ ->
                        finish()
                    }
                    .show()
            }
        }
    }

    private fun initEvent() {
        btnRegister.clicks()
            .map {
                RegisterIntent.ClickRegisterIntent(
                    username = tvNewUsername.text.toString(),
                    mobilephone = tvUserActor.text.toString(),
                    role = role,
                    district = district,
                    categoryCode = categoryCode
                )
            }
            .autoDisposable(scopeProvider)
            .subscribe(registerIntentPublish)
        //role选择事件
        rg_role.setOnCheckedChangeListener { group, checkedId ->
            role = when (checkedId) {
                R.id.rd_manager_right -> {
                    district_layout.visibility = View.GONE
                    district = 0
                    category_layout.visibility = View.GONE
                    categoryCode = "-1"
                    "管理员"
                }
                R.id.rd_account_right -> {
                    district_layout.visibility = View.VISIBLE
                    category_layout.visibility = View.GONE
                    categoryCode = "-1"
                    "库管员"
                }
                R.id.rd_supplier_right -> {
                    district_layout.visibility = View.GONE
                    district = 0
                    category_layout.visibility = View.VISIBLE
                    "供应商"
                }
                else -> ""
            }

        }
        //district单选事件
        rg_district.setOnCheckedChangeListener { group, checkId ->
            district = when (checkId) {
                R.id.rd_xinshinan_district -> 0
                R.id.rd_xishan_district -> 1
                else -> 0
            }
        }
        //categoryCode的spinner选择事件
    }
}