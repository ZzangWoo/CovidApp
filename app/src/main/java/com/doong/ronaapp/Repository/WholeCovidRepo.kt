package com.doong.ronaapp.Repository

import com.doong.ronaapp.Entity.API_Entity.SomeCovidEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface WholeCovidRepo {
    @GET("/total/country/{slug}/status/confirmed")
    fun getStatus(
        @Path("slug") slug: String,
        @QueryMap param: Map<String, String>
    ): Call<List<SomeCovidEntity>>
}