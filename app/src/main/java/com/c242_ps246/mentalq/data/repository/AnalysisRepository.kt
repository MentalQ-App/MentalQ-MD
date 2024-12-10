package com.c242_ps246.mentalq.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c242_ps246.mentalq.data.local.room.AnalysisDao
import com.c242_ps246.mentalq.data.remote.response.ListAnalysisItem
import com.c242_ps246.mentalq.data.remote.retrofit.AnalysisApiService

class AnalysisRepository(
    private val analysisApiService: AnalysisApiService,
    private val analysisDao: AnalysisDao
) {
    fun getAnalysis(): LiveData<Result<Triple<List<ListAnalysisItem>, Int, String?>>> = liveData {
        emit(Result.Loading)
        try {
            val localData = analysisDao.getAllAnalysis().takeLast(28)
            if (localData.isNotEmpty()) {
                val predictedStatusList = localData.map { it.predictedStatus }
                val (size, modePredictedStatus) = calculateMode(predictedStatusList)
                emit(Result.Success(Triple(localData, size, modePredictedStatus)))
            }

            try {
                val response = analysisApiService.getAnalysis()
                val remoteAnalysis = response.listAnalysis

                if (remoteAnalysis != null) {
                    val slicedRemoteData = remoteAnalysis.takeLast(28)
                    val predictedStatusList = slicedRemoteData.map { it.predictedStatus }
                    val (size, modePredictedStatus) = calculateMode(predictedStatusList)
                    val analysisList = slicedRemoteData.map { analysis ->
                        ListAnalysisItem(
                            analysis.id,
                            analysis.noteId,
                            analysis.predictedStatus,
                            analysis.confidenceScore,
                            analysis.updatedAt,
                            analysis.createdAt
                        )
                    }
                    if (localData != analysisList) {
                        analysisDao.clearAllAnalysis()
                        analysisDao.insertAllAnalysis(analysisList)
                    }
                    emit(Result.Success(Triple(analysisList, size, modePredictedStatus)))
                }
            } catch (e: Exception) {
                if (localData.isEmpty()) {
                    emit(Result.Error("Failed to fetch remote data: ${e.message}"))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error("Database error: ${e.message}"))
        }
    }

    private fun calculateMode(values: List<String?>): Pair<Int, String?> {
        val size = values.size

        val mode = values
            .filterNotNull()
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

        return Pair(size, mode)
    }

}