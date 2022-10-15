package com.ti4n.freechat.model.response.freechat

data class FreeChatBaseResponse<T>(val code: String, val data: T, val msg: String)
