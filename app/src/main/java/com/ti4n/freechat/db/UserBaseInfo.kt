package com.ti4n.freechat.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class UserBaseInfo(
    @PrimaryKey val userID: String,
    val nickname: String = "",
    val faceURL: String = "",
    val birth: Long = 0,
    val gender : Int = 1,
    val email : String = "",
    val token: String,
    val expiredTime: Long  // unit: s
)
