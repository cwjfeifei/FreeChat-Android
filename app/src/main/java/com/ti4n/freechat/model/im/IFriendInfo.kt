package com.ti4n.freechat.model.im

import io.openim.android.sdk.models.FriendInfo

data class IFriendInfo(
    val userID: String,
    val nickname: String,
    val faceURL: String,
    val gender: Int,
    val phoneNumber: String,
    val birth: Long,
    val email: String,
    val remark: String,
    val ex: String,
    val createTime: Long,
    val addSource: Int,
    val operatorUserID: String,
)

fun FriendInfo.toIFriendInfo() = IFriendInfo(
    userID,
    nickname,
    faceURL,
    gender,
    phoneNumber,
    birth,
    email,
    remark,
    ex,
    createTime,
    addSource,
    operatorUserID
)
