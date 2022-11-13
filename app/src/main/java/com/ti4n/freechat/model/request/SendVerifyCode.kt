package com.ti4n.freechat.model.request

import io.openim.android.sdk.utils.ParamsUtil

data class SendVerifyCode(
    val UsedFor: Int,
    val userId: String,
    val email: String,
    val operationID: String = ParamsUtil.buildOperationID()
)
