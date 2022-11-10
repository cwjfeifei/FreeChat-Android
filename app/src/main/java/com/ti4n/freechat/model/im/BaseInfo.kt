package com.ti4n.freechat.model.im

import com.ti4n.freechat.util.IM.DEFAULT_FACEURL
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

fun UserInfo.toBaseInfo() = BaseInfo(userID, nickname, remark ?: "", faceURL.ifEmpty { DEFAULT_FACEURL }, birth, gender, email ?: "")
fun FriendInfo.toBaseInfo() =
    BaseInfo(userID, nickname, remark ?: "", faceURL, birth, gender, email)
