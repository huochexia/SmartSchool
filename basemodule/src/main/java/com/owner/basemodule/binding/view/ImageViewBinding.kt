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
package com.owner.basemodule.binding.view

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.request.RequestOptions
import com.owner.basemodule.image.GlideApp

/**
 *对ImageView的自定义属性与设置方法进行绑定
 * Created by Liuyong on 2019-03-23.It's smartschool
 *@description:
 */
/**
 *  将图像的url地址转换成图像
 */
@BindingAdapter("bind_imageView_url")
fun loadImage(imageView: ImageView, url: String?) {
    //GlideApp是@GlideModule注释经编译后形成的类
    GlideApp.with(imageView.context)
        .load(url)
        .into(imageView)
}

/**
 * 将图像变形为圆形
 */
@BindingAdapter("bind_imageView_url_circle")
fun loadImageCircle(imageView: ImageView, url: String?) {
    GlideApp.with(imageView.context)
        .load(url)
        .apply(RequestOptions().circleCrop())
        .into(imageView)
}