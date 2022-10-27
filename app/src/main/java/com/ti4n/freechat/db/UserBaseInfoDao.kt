package com.ti4n.freechat.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserBaseInfoDao {

    @Query("SELECT * FROM userbaseinfo WHERE userId = :address LIMIT 1")
    fun getUserInfo(address: String): Flow<UserBaseInfo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userBaseInfo: UserBaseInfo)
}