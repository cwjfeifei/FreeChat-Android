package com.ti4n.freechat.model.im

import io.openim.android.sdk.models.FriendInfo
import io.openim.android.sdk.models.UserInfo

data class BaseInfo(
    val userID: String,
    val nickname: String,
    val remark: String,
    val faceURL: String,
    val birth: Long,
    val gender: Int,
    val email: String
)

fun UserInfo.toBaseInfo() = BaseInfo(userID, nickname, remark ?: "", faceURL, birth, gender, email)
fun FriendInfo.toBaseInfo() =
    BaseInfo(userID, nickname, remark ?: "", faceURL, birth, gender, email)
