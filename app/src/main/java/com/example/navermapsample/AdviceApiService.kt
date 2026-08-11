package com.example.navermapsample

import retrofit2.Call
import retrofit2.http.GET

interface AdviceApiService {
    @GET("api/advice")
    fun getAdvice(): Call<AdviceResponse>
}

data class AdviceResponse(
    val author: String,
    val authorProfile: String,
    val message: String
)
