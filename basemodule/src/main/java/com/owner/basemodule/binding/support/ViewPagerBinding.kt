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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.owner.basemodule.adapter.ViewPagerAdapter
import com.owner.basemodule.functional.Consumer

/**
 *通过DataBinding为ViewPager设置内容，事件监听器等内容
 * Created by Liuyong on 2019-03-24.It's smartschool
 *@description:
 */
/**
 *
 */
@BindingAdapter(
    "bind_viewPager_fragmentManager",
    "bind_viewPager_fragments",
    "bind_viewPager_offScreenPageLimit", requireAll = false
)
fun setViewPagerAdapter(
    viewPager: ViewPager,
    fragmentManager: FragmentManager,
    fragments: List<Fragment>,
    pageLimit: Int?
) {

    viewPager.adapter = ViewPagerAdapter(fragmentManager, fragments)
    viewPager.offscreenPageLimit = pageLimit ?: 1
}

/**
 * 绑定事件监听器,只改写了一个方法，所以这里只绑定一个属性
 */
@BindingAdapter("bind_viewPager_onPageSelectedChanged", requireAll = false)
fun setOnPageChangeListener(
    viewPager: ViewPager,
    onPageSelected: Consumer<Int>
) {
    viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            onPageSelected.accept(position)
        }

    })
}


