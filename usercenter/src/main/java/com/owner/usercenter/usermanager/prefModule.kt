package com.owner.usercenter.usermanager

import com.goldenstraw.restaurant.goodsmanager.utils.PrefsHelper
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

const val PREFS_MODULE = "PREFS_MODULE"

val prefsModule= Kodein.Module(PREFS_MODULE){

    bind<PrefsHelper>() with provider {
        PrefsHelper(instance())
    }
}