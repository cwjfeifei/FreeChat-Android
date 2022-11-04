package com.ti4n.freechat.model.response

data class UserToken(
    val expiredTime: Long,
    val token: String,
    val userID: String
)