package com.owner.basemodule.network

/**
 * 基本响应对象的接口
 */
interface IBaseResponse<T> {
    fun code(): Int
    fun msg(): String
    fun data(): T
    fun isSuccess(): Boolean
}