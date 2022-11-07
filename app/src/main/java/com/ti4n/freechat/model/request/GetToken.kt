package com.ti4n.freechat.model.request

import io.openim.android.sdk.enums.Platform
import io.openim.android.sdk.utils.ParamsUtil

data class GetToken(
    val userID: String,
    val operationID: String = ParamsUtil.buildOperationID(),
    val platform: Int = Platform.ANDROID,
    val secret: String = "tuoyun",
)