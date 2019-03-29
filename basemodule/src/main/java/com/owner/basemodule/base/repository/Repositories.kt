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
package com.owner.basemodule.base.repository

/**
 * 数据仓库类文件，定义来源不同的基础数据仓库类
 * Created by Liuyong on 2019-03-20.It's smartschool
 *@description:
 */
/**
 * 同时处理来源远程数据和本地数据的基础数据仓库类
 */
open class BaseRepositoryBoth<T : IRemoteDataSource, R : ILocalDataSource>(
    val remoteDataSource: T,
    val localDataSource: R
) : IRepository

/**
 * 处理来自本地数据源的基础数据仓库类
 */
open class BaseRepositoryLocal<T : ILocalDataSource>(
    val localDataSource: T
) : IRepository

/**
 * 处理来自远程数据源的基础数据仓库类
 */
open class BaseRepositoryRemote<T : IRemoteDataSource>(
    val remoteDataSource: T
) : IRepository