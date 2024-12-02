package com.c242_ps246.mentalq.data.remote.retrofit

import com.c242_ps246.mentalq.data.remote.response.PsychologistResponse
import retrofit2.http.GET

interface PsychologistApiService {

    @GET("psychologists")
    suspend fun getPsychologists(): PsychologistResponse
}