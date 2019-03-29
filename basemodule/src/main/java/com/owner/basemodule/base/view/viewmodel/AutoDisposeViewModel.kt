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
package com.owner.basemodule.base.view.viewmodel

import androidx.lifecycle.ViewModel
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 *定义一个能够被AutoDispose对象监听到的ViewModel ,因为在ViewModel中要使用Rx数据流，为了避免内存泄漏，需要在
 * 生命周期结束后可以取消Rx数据流。
 * Created by Liuyong on 2019-03-21.It's smartschool
 *@description:
 */
open class AutoDisposeViewModel : ViewModel(),
    LifecycleScopeProvider<AutoDisposeViewModel.ViewModelEvent> {

    /**
     * ViewModel的生命周期事件：生成和清除
     */
    enum class ViewModelEvent {
        CREATED, CLEARED
    }

    /**
     * 事件发射器
     */
    private val lifecycleEvents = BehaviorSubject.createDefault(ViewModelEvent.CREATED)

    /**
     * 实现以下三个方法，它们是LifecycleScopeProvider接口的方法
     */
    override fun lifecycle(): Observable<ViewModelEvent> = lifecycleEvents.hide()

    override fun correspondingEvents(): CorrespondingEventsFunction<ViewModelEvent> = CORRESPONDING_EVENTS
    /*返回当前生命周期事件状态*/
    override fun peekLifecycle(): ViewModelEvent? = lifecycleEvents.value

    companion object {
        private val CORRESPONDING_EVENTS = CorrespondingEventsFunction<ViewModelEvent> { event->
            when (event) {
                ViewModelEvent.CREATED -> ViewModelEvent.CLEARED
                else -> throw LifecycleEndedException(
                    "清除之后不能在绑定ViewModel。"
                )
            }
        }
    }
    /**
     * 当ViewModel退出时会调用这个方法，此时发射CLEARED事件。当绑定生命周期的AutoDispose对象监听到该
     * 事件后会进行消除，防止内存泄漏
     */
    override fun onCleared() {
        lifecycleEvents.onNext(ViewModelEvent.CLEARED)
        super.onCleared()
    }
}