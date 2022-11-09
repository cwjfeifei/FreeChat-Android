package com.ti4n.freechat.model.im

import io.openim.android.sdk.models.BlacklistInfo
import io.openim.android.sdk.models.FriendInfo
import io.openim.android.sdk.models.PublicUserInfo
import io.openim.android.sdk.models.UserInfo

data class IUserInfo(
    val userID: String,
    val nickname: String,
    val faceURL: String,
    val gender: Int,
    val phoneNumber: String,
    val birth: Long,
    val email: String,
    val ex: String,
    val remark: String,
    val publicInfo: PublicUserInfo?,
    val friendInfo: IFriendInfo,
    val blackInfo: BlacklistInfo?,
    val globalRecvMsgOpt: Int
)

fun UserInfo.toIUserInfo() = IUserInfo(
    userID,
    nickname,
    faceURL,
    gender,
    phoneNumber,
    birth,
    email,
    ex,
    remark,
    publicInfo,
    friendInfo.toIFriendInfo(),
    blackInfo,
    globalRecvMsgOpt
)
