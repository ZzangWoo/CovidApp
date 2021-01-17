package com.doong.covidapp.Entity

class CovidInfo (val countryName: String,
                 val updatedDate: String,
                 val slug: String,
                 var isSmile: Boolean = false,
                 val wholeConfirmedCount: Int,
                 val confirmedCount: Int,
                 val wholeDeathCount: Int,
                 val deathCount: Int
)