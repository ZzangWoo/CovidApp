package com.doong.ronaapp.Repository

import com.doong.ronaapp.Entity.API_Entity.YesterdayCovidEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface YesterdayCovidRepo {
    @GET("/world-overview.json")
    fun getStatus(
        @QueryMap param: Map<String, String>
    ): Call<YesterdayCovidEntity>
}