package com.ti4n.freechat.model.request

import java.util.UUID

data class GetToken(
    val userID: String,
    val operationID: String = UUID.randomUUID().toString(),
    val platform: Int = 7,
    val secret: String = "tuoyun",
)