package com.owner.basemodule.network

/**
 * 每次请求一个数据，返回都会有objectId,updateAt,createAt，
 * 我们就可以根据这些去写一个通用的实体类BaseResp.这里我把code付了一个初值为-1，
 * 然后这个BaseResp是一个抽象类，里面有个抽象方法是isSuccess()，在子类中我们可以用这个判断是否请求成功，
 */
abstract class BaseResp(
    var code: Int = -1, //错误码
    var error: String? = null,
    var objectId: String? = null,
    var createAt: String? = null,
    var updateAt: String? = null
) {
    abstract fun isSuccess(): Boolean
}