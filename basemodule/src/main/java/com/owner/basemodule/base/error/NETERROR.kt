package com.owner.basemodule.base.error

/**
 * 网络错误
 */
enum class NETERROR(private val code:Int, private val err:String) {
    /**
     * 未知错误
     */
    UNKNOWN(1000, "未知错误"),
    /**
     * 解析错误
     */
    PARSE_NETERROR(1001, "解析错误"),
    /**
     * 网络错误
     */
    NETWORD_NETERROR(1002, "网络错误"),
    /**
     * 协议出错
     */
    HTTP_NETERROR(1003, "协议出错"),

    /**
     * 证书出错
     */
    SSL_NETERROR(1004, "证书出错"),

    /**
     * 连接超时
     */
    TIMEOUT_NETERROR(1006, "连接超时");

    fun getValue(): String {
        return err
    }

    fun getKey(): Int {
        return code
    }

}