package com.goldenstraw.restaurant.goodsmanager

import android.content.SharedPreferences
import com.owner.basemodule.util.prefs.int

/**
 * Created by Administrator on 2019/10/30 0030
 */
class PrefsHelper(prefs: SharedPreferences) {

    var showNumber by prefs.int("shownubmer", 10)
}