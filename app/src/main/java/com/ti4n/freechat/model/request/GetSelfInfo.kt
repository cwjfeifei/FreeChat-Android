package com.ti4n.freechat.model.request

import io.openim.android.sdk.utils.ParamsUtil

data class GetSelfInfo(
    val userID: String,
    val operationID: String = ParamsUtil.buildOperationID(),
)