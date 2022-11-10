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

    @Query("SELECT * FROM userbaseinfo LIMIT 1")
    fun getUserInfo(): Flow<UserBaseInfo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userBaseInfo: UserBaseInfo)

    @Query("DELETE  FROM userbaseinfo")
    suspend fun delete()
}