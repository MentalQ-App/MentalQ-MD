package com.c242_ps246.mentalq.data.remote.retrofit

import com.c242_ps246.mentalq.data.remote.response.PsychologistResponse
import com.c242_ps246.mentalq.data.remote.response.SinglePsychologistResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PsychologistApiService {

    @GET("psychologist")
    suspend fun getPsychologists(): PsychologistResponse

    @GET("psychologist/{id}")
    suspend fun getPsychologistById(
        @Path("id") psychologistId: String
    ): SinglePsychologistResponse
}