package com.c242_ps246.mentalq.data.remote.retrofit

import com.c242_ps246.mentalq.data.remote.response.AnalysisResponse
import retrofit2.http.GET

interface AnalysisApiService {

    @GET("analysis")
    suspend fun getAnalysis(): AnalysisResponse
}