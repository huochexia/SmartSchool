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
package com.owner.basemodule.base.view.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.owner.basemodule.BR
import com.owner.basemodule.base.mvi.IIntent
import com.owner.basemodule.base.mvi.IView
import com.owner.basemodule.base.mvi.IViewState

/**
 * 实现对Activity与布局绑定的基本内容，以及MVI架构中IView接口，这里没有实现接口方法，交由子类完成。
 * Created by Liuyong on 2019-03-20.It's smartschool
 *@description:
 */
abstract class BaseActivity<B : ViewDataBinding, I : IIntent, S : IViewState>
    : InjectionActivity(), IView<I, S> {

    private lateinit var binding: B

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initView()
    }

    /**
     * 初始化视图的空方法，由具体类实现
     */
    open fun initView() {

    }

    /**
     *初始化绑定
     */
    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, layoutId)
        with(binding) {
            //因为本工程中Activity视图布局是与Activity对象进行绑定
            // 所以Activity基础类中实现了这个基本变量绑定
            setVariable(BR.activity, this@BaseActivity)
            //为LiveData对象实现生命周期绑定
            lifecycleOwner = this@BaseActivity
        }
    }

}