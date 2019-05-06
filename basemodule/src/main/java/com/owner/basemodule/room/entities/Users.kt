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
package com.owner.basemodule.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 * Created by Liuyong on 2019-04-30.It's smartschool
 *@description:
 */
@Entity
data class User(
    @PrimaryKey val objectId: String? = "",
    @ColumnInfo val username: String? = "",
    @ColumnInfo val mobilePhoneNumber: String? = "",
    @ColumnInfo val avatar: String? = "",
    @ColumnInfo val letters: String = ""
)

data class AllUserResp(val results: MutableList<User>)