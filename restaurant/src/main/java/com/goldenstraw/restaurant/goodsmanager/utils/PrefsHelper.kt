package com.goldenstraw.restaurant.goodsmanager.utils

import android.content.SharedPreferences
import com.owner.basemodule.util.prefs.int
import com.owner.basemodule.util.prefs.string
import com.owner.basemodule.util.prefs.stringSet

/**
 * Created by Administrator on 2019/10/30 0030
 */
class PrefsHelper(prefs: SharedPreferences) {

    var username by prefs.string("username", "")
    var showNumber by prefs.int("shownubmer", 10)
    var categoryCode by prefs.string("categoryCode", "")

    var district by prefs.int("district", 0)

    var role by prefs.string("role","")
}