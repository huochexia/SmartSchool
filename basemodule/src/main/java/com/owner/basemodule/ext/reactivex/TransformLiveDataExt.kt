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
package com.owner.basemodule.ext.reactivex

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.*

/**
 * Rx 转换为 LiveData. 先定义Flowable转换LiveData。然后其他RX转换成Flowable,再转换LiveData.
 * Created by Liuyong on 2019-03-25.It's smartschool
 *@description:
 */

fun <T> Flowable<T>.toLiveData(): LiveData<T> =

    LiveDataReactiveStreams.fromPublisher(this)

fun <T> Observable<T>.toLiveData(
    backpressureStrategy: BackpressureStrategy = BackpressureStrategy.LATEST
): LiveData<T> =
    this.toFlowable(backpressureStrategy).toLiveData()

fun <T> Single<T>.toLiveData(): LiveData<T> =
    this.toFlowable().toLiveData()

fun <T> Maybe<T>.toLiveData(): LiveData<T> =
    this.toFlowable().toLiveData()