package com.goldenstraw.restaurant.goodsmanager.utils

import android.content.SharedPreferences
import com.owner.basemodule.util.prefs.boolean
import com.owner.basemodule.util.prefs.int
import com.owner.basemodule.util.prefs.string

/**
 * Created by Administrator on 2019/10/30 0030
 */
class PrefsHelper(prefs: SharedPreferences) {

    var username by prefs.string("username", "")
    var showNumber by prefs.int("shownubmer", 10)
    var categoryCode by prefs.string("categoryCode", "")

    var district by prefs.int("district", -1)

    var role by prefs.string("role", "")

    var rights by prefs.string("rights", "")

    var autoLogin by prefs.boolean("autoLogin", false)

    var teachers by prefs.int("teachers", 0)
}