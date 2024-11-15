package com.c242_ps246.mentalq.data.remote.retrofit

import com.c242_ps246.mentalq.data.remote.response.NoteResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("notes")
    suspend fun getNotes(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<NoteResponse>

    @GET("notes/{id}")
    suspend fun getNoteById(
        @Query("id") id: String
    ): Response<NoteResponse>

    @POST("notes")
    suspend fun createNote(
        @Query("title") title: String,
        @Query("content") content: String
    ): Response<NoteResponse>
}