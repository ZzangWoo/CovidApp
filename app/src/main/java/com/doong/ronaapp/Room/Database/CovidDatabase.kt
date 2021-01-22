package com.doong.ronaapp.Room.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.doong.ronaapp.Room.DAO.CountryDAO
import com.doong.ronaapp.Room.DAO.LogDAO
import com.doong.ronaapp.Room.Entity.ApiLog
import com.doong.ronaapp.Room.Entity.Country
//, ApiLog::class
@Database(entities = [Country::class, ApiLog::class], version = 2)
abstract class CovidDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDAO
    abstract fun logDao(): LogDAO

    companion object {
        private var INSTANCE : CovidDatabase? = null

        // Singleton
        fun getInstance(context: Context) : CovidDatabase? {
            if (INSTANCE == null) {
                // 중복 방지
                synchronized(CovidDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        CovidDatabase::class.java, "rona.db")
                        .build()
                }
            }
            return INSTANCE
        }
    }
}