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

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.viewModelScope
import com.owner.basemodule.ext.livedata.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


/**
 *ViewModel基础类，继承AutoDisposeViewModel
 * Created by Liuyong on 2019-03-21.It's smartschool
 *@description:
 */
open class BaseViewModel : AutoDisposeViewModel(), Observable {

    private val callbacks by lazy {
        PropertyChangeRegistry()
    }

    val defUI: UIChange by lazy { UIChange() }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    fun notifyAllChanged() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    /**
     *利用协程对网络数据访问，处理错误信息
     */
    /*
      所有网络请求协程都在viewModelScope域中启动，当页面销毁时会自动调用ViewModel的#onCleared方法取消所有协程
     */
    fun launchUI(
        block: suspend CoroutineScope.() -> Unit,
        catch: suspend CoroutineScope.(Throwable) -> Unit = {},
        finally: suspend CoroutineScope.() -> Unit = {}
    ) = viewModelScope.launch {
        try {
            block()
        } catch (e: Throwable) {
            catch(e)
        } finally {
            finally()
        }
    }

    /*
     用流的方式进行网络请求
     */
    fun <T> launchFlow(block: suspend () -> T): Flow<T> {
        return flow {
            emit(block())
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    /**
     * UI事件内部类，包含常用的可观察事件如错误信息的显示，提示信息，刷新事件等。
     * 用于UI观察这个对象变量值的变化，做出相应的显示处理。
     */
    inner class UIChange {
        val showDialog by lazy { SingleLiveEvent<String>() }
        val toastEvent by lazy { SingleLiveEvent<String>() }
        val refreshEvent by lazy { SingleLiveEvent<Void>() }
        val loadingEvent by lazy { SingleLiveEvent<Void>() }
    }
}