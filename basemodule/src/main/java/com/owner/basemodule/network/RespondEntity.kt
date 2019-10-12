package com.owner.basemodule.network


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


