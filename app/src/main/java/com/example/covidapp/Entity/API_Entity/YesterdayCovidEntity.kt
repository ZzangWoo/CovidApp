package com.example.covidapp.Entity.API_Entity

import com.example.covidapp.Room.Entity.Country
import com.google.gson.annotations.SerializedName

data class YesterdayCovidEntity (
    @SerializedName("stats")
    val stats: Map<String, WorldStats>,

    @SerializedName("updates")
    val updates: List<WorldUpdates>
)

data class WorldUpdates (
    @SerializedName("country")
    val country: String

    // 나머지 속성들은 생략
)

data class WorldStats (
    @SerializedName("cases")
    val cases: Int,

    @SerializedName("gmtCasesDelta")
    val gmtCasesDelta: Int,

    @SerializedName("casesDelta")
    val casesDelta: Int,

    @SerializedName("deaths")
    val deaths: Int,

    @SerializedName("gmtDeathsDelta")
    val gmtDeathsDelta: Int,

    @SerializedName("deathsDelta")
    val deathsDelta: Int
)