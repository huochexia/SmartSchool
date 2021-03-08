package com.owner.basemodule.network



/**
 * 基本响应对象的接口
 */
interface IBaseResponse<T> {
    fun code(): Int
    fun error(): String?
    fun data(): T?
    fun isSuccess(): Boolean = code() == 0
}

/**
 * 创建对象响应体
 */
data class CreateObject(
    val code: Int = 0,
    val error: String?,
    val createdAt: String?,
    val objectId: String?
) : IBaseResponse<String> {

    override fun code(): Int = code

    override fun error(): String? = error

    override fun data(): String? = objectId
}

/**
 * 修改对象响应体
 */
data class UpdateObject(
    val code: Int = 0,
    val error: String?,
    val updatedAt: String?
) : IBaseResponse<String> {


    override fun code(): Int = code

    override fun error(): String? = error

    override fun data(): String? = updatedAt
}

/**
 * 删除对象响应体
 */
data class DeleteObject(
    val code: Int = 0,
    val error: String?,
    val msg: String?
) : IBaseResponse<String> {


    override fun code(): Int = code

    override fun error(): String? = error

    override fun data(): String? = msg
}

/**
 * 获取对象列表
 */
class ObjectList<T>(
    val code: Int = 0,
    val error: String?,
    val results: MutableList<T>?
) : IBaseResponse<MutableList<T>> {

    override fun code(): Int = code

    override fun error(): String? = error

    override fun data(): MutableList<T>? = results
}





