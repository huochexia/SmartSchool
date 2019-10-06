package com.owner.basemodule.network

import com.google.gson.JsonSyntaxException
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.functions.Function
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * 解析响应体
 */
/**
 * 1、封装类，用于封装请求数据返回结果
 */
data class HttpResult<T>(
    var code: Int = 0, //如果是错误信息，此处将是错误码
    var error: String? = null,
    var results: T? = null
) {
    fun isSuccess(): Boolean {
        return code == 0
    }

}

/**
 * 2、网络信息常量类
 */
object ResponseCons {
    const val STATUS_SUCCESS = 1
    const val SUCCESS_MSG = "成功"

    const val STATUS_101 = 101
    const val FAILURE_101 = "对象不存在"
}

/**
 * 3、状态码匹配工具
 */
object StatusUtils {
    private val mStatusResult = StatusResult()

    class StatusResult {
        var status: Int = 0
        var desc: String? = null
        var isSuccess: Boolean = false
    }

    fun judgeStatus(status: Int): StatusResult {
        var desc = ""
        var isSuccess = false
        when (status) {
            ResponseCons.STATUS_SUCCESS -> {
                desc = ResponseCons.SUCCESS_MSG
                isSuccess = true
            }
            ResponseCons.STATUS_101 -> desc = ResponseCons.FAILURE_101
        }
        mStatusResult.status = status
        mStatusResult.desc = desc
        mStatusResult.isSuccess = isSuccess
        return mStatusResult
    }
}


/**
 * 4、封装错误码信息
 */
class ApiException(status: Int) : RuntimeException(getErrorDesc(status)) {
    companion object {
        fun getErrorDesc(status: Int): String? {
            return StatusUtils.judgeStatus(status).desc
        }
    }

    /**
     * 5、状态过滤器类
     */
    class StautsFilter<T> : Function<HttpResult<T>, T> {
        override fun apply(t: HttpResult<T>): T {
            if (!t.isSuccess())
                throw ApiException(t.code)
            return t.results as T
        }
    }

    /**
     * 6、将响应数据中的数据提取出来
     */
    fun <T> filterStauts(observable: Observable<HttpResult<T>>): Observable<T> {
        return observable.map {
            StautsFilter<T>().apply(it)
        }
    }

    fun <T> filterStauts(single: Single<HttpResult<T>>): Single<T> {
        return single.map {
            StautsFilter<T>().apply(it)
        }
    }

    fun <T> filterStatus(flowable: Flowable<HttpResult<T>>): Flowable<T> {
        return flowable.map {
            StautsFilter<T>().apply(it)
        }
    }
}

/**
 *7、过滤网络层的错误, 实现onError（）.使用这个自定义类，取代Observer<T>
 */
abstract class FilterObserver<T> : Observer<T> {

    lateinit var error: String
    override fun onError(e: Throwable) {
        error = when (e) {
            is TimeoutException -> "超时了"
            is SocketTimeoutException -> "超时了"
            is ConnectException -> "超时了"
            is JsonSyntaxException -> "Json格式错误"
            else -> e?.message!!
        }
    }

}