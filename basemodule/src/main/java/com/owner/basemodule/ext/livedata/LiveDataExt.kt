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
package com.owner.basemodule.ext.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Scheduler
import io.reactivex.android.MainThreadDisposable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * LiveData 转换为 RX
 * Created by Liuyong on 2019-03-25.It's smartschool
 *@description:
 */

fun <T> LiveData<T>.toReactiveStream(
    observerScheduler: Scheduler = AndroidSchedulers.mainThread()
): Flowable<T> = Flowable.create({ emitter: FlowableEmitter<T> ->
    val observer = Observer<T> { data ->
        data?.let {
            emitter.onNext(it)
        }
    }

    observeForever(observer)

    emitter.setCancellable {
        object : MainThreadDisposable() {
            override fun onDispose() {
                removeObserver(observer)
            }
        }
    }

}, BackpressureStrategy.LATEST)
    .subscribeOn(AndroidSchedulers.mainThread())
    .observeOn(observerScheduler)

fun <X, Y> LiveData<X>.map(function: (X) -> Y): LiveData<Y> =
    Transformations.map(this, function)

fun <X, Y> LiveData<X>.switchMap(function: (X) -> LiveData<Y>): LiveData<Y> =
    Transformations.switchMap(this, function)