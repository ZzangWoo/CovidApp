package com.doong.covidapp.Room.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CountryTable")
data class Country (
    @PrimaryKey
    @ColumnInfo(name = "countryName") var countryName: String // 나라이름
)