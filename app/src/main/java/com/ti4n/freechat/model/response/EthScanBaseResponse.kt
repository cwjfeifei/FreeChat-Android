package com.ti4n.freechat.model.response

data class EthScanBaseResponse<T>(val status: Int, val message: String, val result: T)
