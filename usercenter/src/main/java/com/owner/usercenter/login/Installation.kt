package com.owner.usercenter.login


data class Installation(
    var deviceType: String = "android",
    var installationId: String,
    var user: String
)