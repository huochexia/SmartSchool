package com.owner.basemodule.util

sealed class ReturnResult {

    class Success<T>(val value:T):ReturnResult()
    class Failure(val e:String):ReturnResult()
}