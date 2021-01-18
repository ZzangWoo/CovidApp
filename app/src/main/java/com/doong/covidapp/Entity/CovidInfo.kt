package com.doong.covidapp.Entity

class CovidInfo (val countryName: String,
                 val updatedDate: String,
                 val test: Boolean,
                 val wholeConfirmedCount: Int,
                 val confirmedCount: Int,
                 val wholeDeathCount: Int,
                 val deathCount: Int
)