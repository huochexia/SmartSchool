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
package com.owner.usercenter.util

import android.content.SharedPreferences
import com.owner.basemodule.util.prefs.boolean
import com.owner.basemodule.util.prefs.int
import com.owner.basemodule.util.prefs.string
import com.owner.basemodule.util.prefs.stringSet

/**
 *
 * Created by Liuyong on 2019-04-01.It's smartschool
 *@description:
 */
class PrefsHelper(prefs: SharedPreferences) {

    var autoLogin by prefs.boolean("autoLogin", false)

    var username by prefs.string("username", "")

    var password by prefs.string("password", "")

    var sessionToken by prefs.string("sessionToken", "")

    var objectId by prefs.string("objectId", "")

    var categoryCode by prefs.string("categoryCode", "")

    var district by prefs.int("district", 0)

    var roles by prefs.stringSet("roles")

}