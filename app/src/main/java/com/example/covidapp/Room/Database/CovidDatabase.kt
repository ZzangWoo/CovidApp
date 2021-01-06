package com.example.covidapp.Room.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.covidapp.Room.DAO.CountryDAO
import com.example.covidapp.Room.Entity.Country

@Database(entities = [Country::class], version = 1)
abstract class CovidDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDAO

    companion object {
        private var INSTANCE : CovidDatabase? = null

        // Singleton
        fun getInstance(context: Context) : CovidDatabase? {
            if (INSTANCE == null) {
                // 중복 방지
                synchronized(CovidDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        CovidDatabase::class.java, "covid.db")
                        .build()
                }
            }
            return INSTANCE
        }
    }
}