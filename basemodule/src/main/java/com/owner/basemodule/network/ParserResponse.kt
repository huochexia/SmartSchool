package com.owner.basemodule.network

import com.google.gson.JsonSyntaxException
import io.reactivex.Observable
import io.reactivex.functions.Function
import org.reactivestreams.Subscriber
import java.lang.RuntimeException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * 解析响应体
 */
/**
 * 封装类，用于封装请求数据返回结果
 */
class HttpResult<T>(
    var code: Int = -1, //如果是错误信息，此处将是错误码
    var error: String? = null,
    var data: T? = null
) {
    fun isSuccess(): Boolean {
        return code == -1 && data != null
    }

}

/**
 * 封装错误码信息
 */
class ApiException() : RuntimeException() {
    constructor(errorCode: Int) : this() {
        getApiExceptionMsg(errorCode)
    }

    constructor(message: String) : this() {
        message
    }

    companion object {
        fun getApiExceptionMsg(errorCode: Int) = when (errorCode) {
            101 -> "对象不存在"
            107 -> "时间格式不正确"
            108 -> "必须有用户名和密码"
            202 -> "用户名已存在"
            207 -> "验证码错误"
            209 -> "手机号码已存在"
            210 -> "旧密码不正确"
            else -> "未知错误"
        }

    }
}

/**
 * 状态过滤器类
 */
class StautsFilter<T> : Function<HttpResult<T>, T> {
    override fun apply(t: HttpResult<T>): T {
        if (!t.isSuccess())
            throw ApiException(t.code)
        return t.data as T
    }
}

/**
 * 将响应数据中的数据提取出来
 */
fun <T> filterStauts(observable: Observable<HttpResult<T>>): Observable<T> {
    return observable.map {
        StautsFilter<T>().apply(it)
    }
}

/**
 *过滤网络层的错误, 实现onError（），
 */
abstract class FilterSubscriber<T> : Subscriber<T> {
    lateinit var error: String
    override fun onError(t: Throwable?) {
        error = when (t) {
            is TimeoutException -> "超时了"
            is SocketTimeoutException -> "超时了"
            is ConnectException -> "超时了"
            is JsonSyntaxException -> "Json格式错误"
            else -> t?.message!!
        }

    }
}