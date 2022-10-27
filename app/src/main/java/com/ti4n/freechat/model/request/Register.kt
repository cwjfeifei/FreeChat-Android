package com.ti4n.freechat.model.request

import java.util.UUID

data class Register(
    val UserID: String,
    val birth: Long,
    val faceURL: String,
    val gender: Int,
    val nickname: String,
    val password: String,
    val secret: String = "tuoyun",
    val phoneNumber: String = "",
    val platform: Int = 7,
    val operationID: String = UUID.randomUUID().toString(),
    val email: String = "",
    val ex: String = ""
)