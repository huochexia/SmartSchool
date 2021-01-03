package com.owner.basemodule.base.error

import com.owner.basemodule.base.error.HttpError.*
import com.owner.basemodule.util.toast

/**
 * 返回服务器响应体中定义的异常，一般服务器对于请求都存在响应码，客户端根据响应码去做出相应的处理，
 * 不同的错误码会有不同的日志回馈或者提示。
 * 这一切都是建立在请求成功的基础之上。
 */
sealed class HttpResponse

data class Success<out T>(val data: T) : HttpResponse()

data class Failure(val error: HttpError) : HttpResponse()

/**
 * 响应层错误以及处理方法
 */
enum class HttpError(private val code: Int, private val err: String) {
    /**
     * 未知错误
     */
    UNKNOWN(1000, "未知错误"),

    /**
     * 解析错误
     */
    PARSE_HttpError(1001, "解析错误"),

    /**
     * 网络错误
     */
    NETWORD_HttpError(1002, "网络错误"),

    /**
     * 协议出错
     */
    HTTP_HttpError(1003, "协议出错"),

    /**
     * 证书出错
     */
    SSL_HttpError(1004, "证书出错"),

    /**
     * 连接超时
     */
    TIMEOUT_HttpError(1006, "连接超时");

    fun getValue(): String {
        return err
    }

    fun getKey(): Int {
        return code
    }

}

fun handlingApiException(e: HttpError) {
    when (e) {
        UNKNOWN -> TODO()
        PARSE_HttpError -> TODO()
        NETWORD_HttpError -> TODO()
        HTTP_HttpError -> TODO()
        SSL_HttpError -> TODO()
        TIMEOUT_HttpError -> TODO()
    }
}

/**
 * 处理响应体
 * @successBlock 成功
 * @failureBlock 失败
 */
fun <T> handlingHttpResponse(
    res: HttpResponse,
    successBlock: (data: T) -> Unit,
    failureBlock: ((error: HttpError) -> Unit)? = null
) {
    when (res) {
        is Success<*> -> {
            successBlock.invoke(res.data as T)
        }
        is Failure -> {
            with(res) {
                failureBlock?.invoke(error) ?: defaultErrorBlock.invoke(error)
            }
        }
    }
}

val defaultErrorBlock: (error: HttpError) -> Unit = { error ->
    toast { error.toString() }
}