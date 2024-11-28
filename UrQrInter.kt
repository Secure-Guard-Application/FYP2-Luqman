package com.example.secureguardapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UrQrInter {

    @Headers("x-rapidapi-key: 23f79e0a40msh8a2581544671077p181012jsne416d7ccca9b",
        "x-rapidapi-host:malicious-scanner.p.rapidapi.com")
    @GET("url")
    fun scanDomain(@Query("url") query: String): Call<UrQrApi>


}