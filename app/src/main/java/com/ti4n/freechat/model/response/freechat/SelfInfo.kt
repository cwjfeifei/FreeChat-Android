package com.ti4n.freechat.model.response.freechat

data class SelfInfo(
    val appMangerLevel: Int,
    val birth: Long,
    val faceURL: String,
    val nickname: String,
    val userID: String,
    val email:String,
    val gender: Int
)