package com.ti4n.freechat.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RecentTransfer::class, UserBaseInfo::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun recentTransferDao(): RecentTransferDao
    abstract fun userBaseInfoDao(): UserBaseInfoDao
}