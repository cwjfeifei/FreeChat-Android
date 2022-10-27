package com.ti4n.freechat.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class RecentTransfer(
    @PrimaryKey val toAddress: String,
    val date: Date
)
