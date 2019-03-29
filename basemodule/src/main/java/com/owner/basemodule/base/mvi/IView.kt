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
package com.owner.basemodule.base.mvi

import io.reactivex.Observable

/**
 * MVI架构中，视图层要实现的接口，它主要两个方法：一个是将用户意图（事件）传出；
 * 一个是将得到的状态进行渲染（显示给用户）
 * Created by Liuyong on 2019-03-20.It's smartschool
 *@description:
 */
interface IView<T: IIntent,S: IViewState> {

    fun intents():Observable<T>

    fun rinder(state:S)
}