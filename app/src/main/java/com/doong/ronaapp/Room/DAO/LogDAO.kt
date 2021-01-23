package com.doong.ronaapp.Room.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.doong.ronaapp.Room.Entity.ApiLog

@Dao
interface LogDAO {
    @Insert(onConflict = REPLACE)
    suspend fun logInsert(apiLog: ApiLog)

    @Query("SELECT * FROM ApiLogTable WHERE countryName = :name AND date = :date")
    suspend fun logSelectCount(name: String, date: String) : ApiLog

    @Query("SELECT * FROM ApiLogTable")
    suspend fun logSelect() : ApiLog
}