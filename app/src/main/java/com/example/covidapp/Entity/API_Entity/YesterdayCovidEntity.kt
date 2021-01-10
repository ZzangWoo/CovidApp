package com.example.covidapp.Entity.API_Entity

import com.google.gson.annotations.SerializedName

data class YesterdayCovidEntity (
    @SerializedName("stats")
    val stats: Map<String, WorldStats>
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