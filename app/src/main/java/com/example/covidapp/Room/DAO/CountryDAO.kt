package com.example.covidapp.Room.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.covidapp.Room.Entity.Country

@Dao
interface CountryDAO {
    @Query("SELECT * FROM CountryTable")
    suspend fun countrySelect() : List<Country>

    @Query("SELECT * FROM CountryTable WHERE countryName = :name")
    suspend fun countrySelectItem(name: String) : Country

    @Delete
    suspend fun countryDelete(country: Country)

    @Insert(onConflict = REPLACE)
    suspend fun countryInsert(country: Country)
}