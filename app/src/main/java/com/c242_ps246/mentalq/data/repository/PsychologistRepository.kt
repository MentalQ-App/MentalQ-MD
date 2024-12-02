package com.c242_ps246.mentalq.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c242_ps246.mentalq.data.remote.response.PsychologistItem
import com.c242_ps246.mentalq.data.remote.retrofit.PsychologistApiService

class PsychologistRepository(
    private val psychologistApiService: PsychologistApiService
) {

    fun getPsychologists(): LiveData<Result<List<PsychologistItem>>> = liveData {
        emit(Result.Loading)

        try {
            val response = psychologistApiService.getPsychologists()
            val psychologists = response.listPsychologists

            if (psychologists != null) {
                emit(Result.Success(psychologists))
            } else {
                emit(Result.Error("Failed to fetch remote data"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Database error: ${e.message}"))
        }
    }

}