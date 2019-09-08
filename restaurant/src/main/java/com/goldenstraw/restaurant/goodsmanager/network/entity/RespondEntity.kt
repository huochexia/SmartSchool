package com.goldenstraw.restaurant.goodsmanager.network.entity

import com.owner.basemodule.network.BaseResp

/**
 * 生成商品后返回对象
 */
class createResponse() : BaseResp() {

    override fun isSuccess(): Boolean {
        //code为默认值说明没有错误码，同时objectId有数据，说明生成数据成功
        return code == -1 && objectId.isNullOrEmpty().not()
    }

}