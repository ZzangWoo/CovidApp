package com.doong.ronaapp.Room.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ApiLogTable")
data class ApiLog (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "logID")
    var logID: Long,

    @ColumnInfo(name = "countryName")
    var countryName: String,

    @ColumnInfo(name = "date")
    var date: String
)