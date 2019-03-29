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
 * MVI架构中ViewModel要实现的接口，它主要两个方法，一个接收意图（事件）并处理它们；
 * 一个是将处理意图（事件）得到的状态（model)返回。
 * Created by Liuyong on 2019-03-20.It's smartschool
 *@description:
 */
interface IViewModel<I: IIntent,S: IViewState> {

    fun processIntents(intent: Observable<I>)

    fun states():Observable<S>
}