package com.owner.basemodule.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

/***************************************************************
 *响应层的处理：
 * 网络数据的获取成功后，会有两种情况一是正确得到数据，一是得到数据错误提示
 **************************************************************/
/*
 过滤响应结果,访问成功将交给协程处理，失败抛出封装异常对象
  */
suspend fun <T> parserResponse(
    response: IBaseResponse<T>,
    success: suspend CoroutineScope.(T) -> Unit
) {
    coroutineScope {
        if (response.isSuccess()) success(response.data()!!)
        else throw ResponseThrowable(response.code(), response.error()!!)
    }
}

/*
 * 网络信息常量类
 */
object ResponseCons {
    const val STATUS_SUCCESS = 1
    const val SUCCESS_MSG = "成功"

    const val STATUS_101 = 101
    const val FAILURE_101 = "对象不存在"

    //TODO:补充响应错误码
}

/*
 * 状态码匹配工具
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
class ApiException(val status: Int) : RuntimeException(getErrorDesc(status)) {

    companion object {
        fun getErrorDesc(status: Int): String? {
            return StatusUtils.judgeStatus(status).desc
        }
    }
}

