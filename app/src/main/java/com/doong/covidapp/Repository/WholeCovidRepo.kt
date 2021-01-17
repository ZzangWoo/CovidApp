package com.doong.covidapp.Repository

import com.doong.covidapp.Entity.API_Entity.SomeCovidEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface WholeCovidRepo {
    @GET("/total/country/{slug}/status/confirmed")
    suspend fun getStatus(
        @Path("slug") slug: String,
        @QueryMap param: Map<String, String>
    ): Call<List<SomeCovidEntity>>
}