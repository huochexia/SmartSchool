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
package com.owner.basemodule.base.viewmodel

import com.owner.basemodule.base.mvi.IIntent
import com.owner.basemodule.base.mvi.IViewModel
import com.owner.basemodule.base.mvi.IViewState
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 *ViewModel基础类，继承AutoDisposeViewModel,IViewModel接口。因为是抽象类，所以没有实现IViewModel接口方法，
 * 由具体类去实现
 * Created by Liuyong on 2019-03-21.It's smartschool
 *@description:
 */
abstract class BaseViewModel<I: IIntent,S: IViewState> : AutoDisposeViewModel(),
    IViewModel<I, S> {
    //由具体类实现的方法，汇聚各种数据流
    abstract fun compose():Observable<S>

}