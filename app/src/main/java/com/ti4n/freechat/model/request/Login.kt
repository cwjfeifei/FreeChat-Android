package com.ti4n.freechat.model.request

import io.openim.android.sdk.enums.Platform
import io.openim.android.sdk.utils.ParamsUtil

data class Login(
    val userId: String,
    val verificationCode: String,
    val platform: Int = Platform.ANDROID,
    val phoneNumber: String = "",
    val areaCode: String = "+86",
    val operationID: String = ParamsUtil.buildOperationID()
)
