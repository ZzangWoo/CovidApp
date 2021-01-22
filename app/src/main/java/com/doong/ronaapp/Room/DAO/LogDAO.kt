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

    @Query("SELECT COUNT(*) FROM ApiLogTable WHERE countryName = :name")
    suspend fun logSelectCount(name: String) : Int
}