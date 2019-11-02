package com.goldenstraw.restaurant.goodsmanager.http.entities

import cn.bmob.v3.BmobInstallation

data class PushWhere(val installationId: String)

data class PushData(val alert: String)

data class PushBody(val where: PushWhere, val data: PushData)

