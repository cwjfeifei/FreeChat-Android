package com.ti4n.freechat.model.request

import io.openim.android.sdk.enums.Platform
import io.openim.android.sdk.utils.ParamsUtil
import java.util.UUID

data class Register(
    val userID: String = "",
    val birth: Long = 0,
    val faceURL: String = "",
    val gender: Int = 2,
    val nickname: String = "",
    val secret: String = "tuoyun",
    val phoneNumber: String = "",
    val platform: Int = Platform.ANDROID, // iOS 1, Android 2, Windows 3, OSX 4, WEB 5, 小程序 6，linux 7
    val operationID: String = ParamsUtil.buildOperationID(),
    val email: String = "",
    val ex: String = ""
)