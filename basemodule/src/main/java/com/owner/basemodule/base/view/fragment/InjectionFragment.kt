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
package com.owner.basemodule.base.view.fragment

import androidx.fragment.app.Fragment
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinContext
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.kcontext

/**
 *
 * Created by Liuyong on 2019-03-20.It's smartschool
 *@description:
 */
abstract class InjectionFragment : AutoDisposeFragment(),KodeinAware{

    protected val parentKodein by closestKodein()

    override val kodeinContext: KodeinContext<*>
        get() = kcontext<Fragment>(this)
}