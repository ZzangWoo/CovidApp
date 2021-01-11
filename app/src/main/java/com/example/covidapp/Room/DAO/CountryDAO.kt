package com.example.covidapp.Room.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.covidapp.Room.Entity.Country

@Dao
interface CountryDAO {
    @Query("SELECT * FROM CountryTable")
    fun countryLiveSelect() : LiveData<MutableList<Country>>

    @Query("SELECT * FROM CountryTable")
    fun countrySelect() : MutableList<Country>

    @Query("SELECT * FROM CountryTable WHERE countryName = :name")
    suspend fun countrySelectItem(name: String) : Country

    @Delete
    fun countryDelete(country: Country)

    @Update
    fun countryUpdate(country: Country)

    @Insert(onConflict = REPLACE)
    suspend fun countryInsert(country: Country)
}