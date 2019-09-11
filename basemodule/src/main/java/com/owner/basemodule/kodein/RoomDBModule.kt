package com.owner.basemodule.kodein

import com.owner.basemodule.room.AppDatabase
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

/**
 * Room的数据库为全局统一，所以可以做为全局性的单例
 *
 */

const val ROOM_DATABASE_MODULE_TAG = "ROOM_DATABASE_MODULE_TAG"

val roomDBModule = Kodein.Module(ROOM_DATABASE_MODULE_TAG) {

    bind<AppDatabase>() with singleton { AppDatabase.getInstance(instance()) }
}