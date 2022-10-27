package com.ti4n.freechat.model.response

data class BaseResponse<T>(val errCode: Int, val errMsg: String, val data: T?)
