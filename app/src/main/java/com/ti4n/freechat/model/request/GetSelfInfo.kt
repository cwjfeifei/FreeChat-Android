package com.ti4n.freechat.model.request

import java.util.UUID

data class GetSelfInfo(
    val userID: String,
    val operationID: String = UUID.randomUUID().toString(),
)