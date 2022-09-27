package com.ti4n.freechat.model.response

data class UserToken(
    val expiredTime: Int,
    val token: String,
    val userID: String
)