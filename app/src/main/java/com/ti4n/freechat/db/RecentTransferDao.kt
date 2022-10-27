package com.ti4n.freechat.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecentTransferDao {

    @Query("SELECT * FROM recenttransfer ORDER BY date")
    fun getAllAddress(): PagingSource<Int, RecentTransfer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transfer: RecentTransfer)
}