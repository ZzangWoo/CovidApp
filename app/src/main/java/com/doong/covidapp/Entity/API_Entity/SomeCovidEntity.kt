package com.doong.covidapp.Entity.API_Entity

import com.google.gson.annotations.SerializedName

data class SomeCovidEntity (
    @SerializedName("Country")
    val country: String,

    @SerializedName("Cases")
    val cases: Int,

    @SerializedName("Status")
    val status: String,

    @SerializedName("Date")
    val date: String
)