package com.ti4n.freechat.model.request

data class Register(
    val address: String,
    val birth: Long,
    val faceURL: String,
    val gender: Int,
    val nickname: String,
    val password: String,
    val secret: String,
    val phoneNumber: String = "",
    val platform: Int = 7,
    val operationID: String = "",
    val email: String = "",
    val ex: String = ""
)