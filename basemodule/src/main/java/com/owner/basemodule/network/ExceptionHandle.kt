package com.owner.basemodule.network

import android.util.MalformedJsonException
import com.google.gson.JsonParseException
import com.owner.basemodule.network.HttpError.*
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 统一分析网络访问失败产生的各种异常，然后将这些异常包装成ResponseThrowable对象。
 */
object ExceptionHandle {

    fun handleException(e: Throwable): ResponseThrowable {
        val ex: ResponseThrowable
        if (e is HttpException) {
            ex = ResponseThrowable(HTTP_HttpError, e)
        } else if (e is JsonParseException || e is JSONException || e is MalformedJsonException) {
            ex = ResponseThrowable(PARSE_HttpError, e)
        } else if (e is ConnectException) {
            ex = ResponseThrowable(NETWORD_HttpError, e)
        } else if (e is javax.net.ssl.SSLException) {
            ex = ResponseThrowable(SSL_HttpError, e)
        } else if (e is SocketTimeoutException) {
            ex = ResponseThrowable(TIMEOUT_HttpError, e)
        } else if (e is UnknownHostException) {
            ex = ResponseThrowable(TIMEOUT_HttpError, e)
        } else if (e is ApiException) {//这里是响应层错误
            ex = ResponseThrowable(e.status, e.message!!, e)
        } else {
            ex = ResponseThrowable(UNKNOWN, e)
        }
        return ex
    }
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

/**
 * 请求层错误
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