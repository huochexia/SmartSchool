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
package com.owner.basemodule.binding.support

import android.annotation.SuppressLint
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.databinding.BindingAdapter
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import io.reactivex.functions.Consumer

/**
 *
 * Created by Liuyong on 2019-03-23.It's smartschool
 *@description:
 */
@BindingAdapter("bind_hintColor", "bind_textColor", requireAll = false)
fun setHintColor(searchView: SearchView, hintColor: Int, textColor: Int) {
    searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text).apply {
        setTextColor(textColor)
        setHintTextColor(hintColor)
    }
}

/**
 * 绑定监听事件。SearchView的onQueryTextListener接口两个回调方法：onQueryTextSubmit和onQueryTextChange,
 * 一个是点击搜索按钮，一个改变搜索内容。RxSearchView则定义了SearchViewQueryTextEvent类，它有三个属性，
 * 一个是isSumbitted，一个是queryText,一个是SearchView。通过isSumbitted来判断是那种搜索方式。
 */
@SuppressLint("CheckResult")
@BindingAdapter("bind_onQuerySubmit", "bind_onQueryChanged", requireAll = false)
fun setSearchViewQueryText(
    searchView: SearchView,
    onSubmit: Consumer<String>?,
    onChange: Consumer<String>?
) {

    searchView.queryTextChangeEvents()
        .subscribe {
            if (it.isSubmitted) {
                onSubmit?.accept(it.queryText.toString())
            } else {
                onChange?.accept(it.queryText.toString())
            }
        }
}