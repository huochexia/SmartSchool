package com.owner.basemodule.network

import android.util.MalformedJsonException
import com.google.gson.JsonParseException
import com.owner.basemodule.base.error.HttpError.*
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
        } else {
            ex = if (!e.message.isNullOrEmpty()) ResponseThrowable(1000, e.message!!, e)
            else ResponseThrowable(UNKNOWN, e)
        }

        return ex
    }
}