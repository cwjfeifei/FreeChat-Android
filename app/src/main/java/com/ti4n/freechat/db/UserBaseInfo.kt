package com.ti4n.freechat.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserBaseInfo(
    @PrimaryKey val userId: String,
    val nickname: String,
    val token: String,
    val avatar: String,
    val birthday: Long
)
