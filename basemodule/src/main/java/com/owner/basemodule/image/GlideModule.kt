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
package com.owner.basemodule.image

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule

/**
 *Glide图像处理工具，需要@GlideModule注释
 * Created by Liuyong on 2019-03-23.It's smartschool
 *@description:
 */
@GlideModule
class GlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

    }

    /**
     * 配置应用缓存
     */
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(
            InternalCacheDiskCacheFactory(
                context,
                diskCacheFolderName(context),
                diskCacheSizeBytes()
            )
        ).setMemoryCache(LruResourceCache(memoryCacheSizeBytes().toLong()))
    }

    /**
     *实现及其依赖项迁移到Glide的注释处理器后，返回false。
     */
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
    /**
     * 设置硬盘缓存大小，单位；byte
     */
    private fun diskCacheSizeBytes(): Long {
        return 1024 * 1024 * 512 //512M
    }

    /**
     * 设置硬盘缓存文件名
     */
    private fun diskCacheFolderName(context: Context): String {
        return "smart_school"
    }

    /**
     * 设置内存缓存大小，单位：byte
     */
    private fun memoryCacheSizeBytes(): Int {
        return 1024 * 1024 * 20 //20MB
    }
}