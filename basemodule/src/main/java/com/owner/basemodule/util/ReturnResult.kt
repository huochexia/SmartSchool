package com.owner.basemodule.util

/**
 * 顶层类，用于返回网络结果。
 */
sealed class ReturnResult {

    class Success<T>(val value:T):ReturnResult()
    class Failure(val e:String):ReturnResult()
}