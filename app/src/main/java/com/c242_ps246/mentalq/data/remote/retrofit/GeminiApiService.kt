package com.c242_ps246.mentalq.data.remote.retrofit

import com.c242_ps246.mentalq.data.remote.response.GeminiRequest
import com.c242_ps246.mentalq.data.remote.response.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {

    @POST("/v1beta/models/gemini-1.5-flash-latest:generateContent")
    suspend fun normalizeText(
        @Query("key") apiKey: String,
        @Body requestBody: GeminiRequest
    ): GeminiResponse
}