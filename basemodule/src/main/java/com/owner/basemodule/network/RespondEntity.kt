package com.owner.basemodule.network

import com.owner.basemodule.base.error.HttpError


/**
 * 创建对象响应体
 */
data class CreateObject(
    val code: Int = 0,
    val error: String?,
    val createdAt: String?,
    val objectId: String?
) {
    fun isSuccess(): Boolean = code == 0 && objectId.isNullOrEmpty().not()
}

/**
 * 修改对象响应体
 */
data class UpdateObject(
    val code: Int = 0,
    val error: String?,
    val updatedAt: String?
) {
    fun isSuccess(): Boolean = code == 0 && updatedAt.isNullOrEmpty().not()
}

/**
 * 删除对象响应体
 */
data class DeleteObject(
    val code: Int = 0,
    val error: String?,
    val msg: String?
) {
    fun isSuccess(): Boolean = code == 0 && msg == "ok"
}

/**
 * 获取对象列表
 */
class ObjectList<T>(
    val code: Int = 0,
    val error: String?,
    val results: MutableList<T>?
) {
    fun isSuccess(): Boolean = code == 0
}


/**
 * 网络访问失败产生的异常封装类，通过这个封装类对异常统一处理。
 */
class ResponseThrowable : Exception {
    var code: Int
    var errMsg: String

    constructor(HttpError: HttpError, e: Throwable? = null) : super(e) {
        code = HttpError.getKey()
        errMsg = HttpError.getValue()
    }

    constructor(code: Int, msg: String, e: Throwable? = null) : super(e) {
        this.code = code
        this.errMsg = msg

    }

}


