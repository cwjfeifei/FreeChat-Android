package com.ti4n.freechat.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class UserBaseInfo(
    @PrimaryKey val userID: String,
    var nickname: String = "",
    var faceURL: String = "",
    var birth: Long = 0,
    var gender : Int = 1,
    var email : String = "",
    var token: String,
    var expiredTime: Long  // unit: s
)
