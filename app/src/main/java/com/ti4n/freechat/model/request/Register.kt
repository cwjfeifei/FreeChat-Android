package com.ti4n.freechat.model.request

import java.util.UUID

data class Register(
    val userID: String = "",
    val birth: Long = 0,
    val faceURL: String = "",
    val gender: Int = 1,
    val nickname: String = "",
    val secret: String = "tuoyun",
    val phoneNumber: String = "",
    val platform: Int = 2, // iOS 1, Android 2, Windows 3, OSX 4, WEB 5, 小程序 6，linux 7
    val operationID: String = UUID.randomUUID().toString(),
    val email: String = "",
    val ex: String = ""
)