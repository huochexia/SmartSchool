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

import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.owner.basemodule.functional.Consumer


/**
 *
 * Created by Liuyong on 2019-03-24.It's smartschool
 *@description:
 */
/**
 * 为TabLayout与ViewPager建立关系
 */
@BindingAdapter("bind_tabLayout_viewPager", requireAll = true)
fun setTabLayoutWithViewPager(tabLayout: TabLayout, viewPager: ViewPager) {

    tabLayout.setupWithViewPager(viewPager)
}

/**
 * 为TabLayout绑定选择事件方法，因为TabLayout选择事件有三个方法，所以这里定义了三个接口方法分别对应事件方法
 */
@BindingAdapter("bind_tabLayout_onTabSelected",
    "bind_tabLayout_onTabUnselected",
    "bind_tabLayout_onTabReselected",
    requireAll = false)
fun setTabLayoutSelectedListener(
    tabLayout:TabLayout,
    onTabSelectedListener: Consumer<Int>?,
    onTabUnselectedListener:Consumer<Int>?,
    onTabReselectedListener:Consumer<Int>?
){
    tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
        override fun onTabReselected(tab: TabLayout.Tab?) {
            tab?.let{
                onTabSelectedListener?.accept(it.position)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            tab?.let {
                onTabUnselectedListener?.accept(it.position)
            }
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let {
                onTabReselectedListener?.accept(it.position)
            }
        }

    })
}