package com.ti4n.freechat.model.request

import io.openim.android.sdk.enums.Platform
import java.util.UUID

data class GetToken(
    val userID: String,
    val operationID: String = UUID.randomUUID().toString(),
    val platform: Int = Platform.ANDROID,
    val secret: String = "tuoyun",
)