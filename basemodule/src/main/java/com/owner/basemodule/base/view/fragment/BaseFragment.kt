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
package com.owner.basemodule.base.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.owner.basemodule.BR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * 定义了数据绑定的基础内容,
 * Created by Liuyong on 2019-03-20.It's smartschool
 *@description:
 */
abstract class BaseFragment<B : ViewDataBinding>
    : InjectionFragment() ,CoroutineScope by MainScope(){

    private var mRootView: View? = null

    protected lateinit var mBinding: B

    abstract val layoutId: Int //由具体子类提供，所以子类必须实现这个变量



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mRootView = LayoutInflater.from(context).inflate(layoutId, container, false)

        return mRootView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding(view)
        initView()
    }


    /*
    对外暴露的空方法，具体类可以改写
     */
    open fun initView() {

    }

    private fun initBinding(rootView: View) {
        //因为不知道具体布局的绑定类是什么，所以这里使用DataBindingUtil来实现绑定对象
        mBinding = DataBindingUtil.bind(rootView)!!
        with(mBinding) {

            //因为本工程中Fragment视图布局绑定的变量均有对应的Fragment对象，
            //所以Fragment基础类中实现了这个基本变量绑定
            setVariable(BR.fragment, this@BaseFragment)

            //为LiveData对象实现生命周期绑定
            lifecycleOwner = this@BaseFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRootView = null
        mBinding.unbind() //视图消毁时解除数据绑定
    }
}