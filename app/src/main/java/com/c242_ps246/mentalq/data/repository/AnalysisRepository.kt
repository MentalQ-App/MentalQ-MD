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
    fun getAnalysis(): LiveData<Result<Pair<List<ListAnalysisItem>, String?>>> = liveData {
        emit(Result.Loading)
        try {
            val localData = analysisDao.getAllAnalysis()
            if (localData.isNotEmpty()) {
                val predictedStatusList = localData.map { it.predictedStatus }
                val modePredictedStatus = calculateMode(predictedStatusList)
                emit(Result.Success(Pair(localData, modePredictedStatus)))
            }

            try {
                val response = analysisApiService.getAnalysis()
                val remoteAnalysis = response.listAnalysis

                if (remoteAnalysis != null) {
                    val predictedStatusList = remoteAnalysis.map { it.predictedStatus }
                    val modePredictedStatus = calculateMode(predictedStatusList)
                    val analysisList = remoteAnalysis.map { analysis ->
                        ListAnalysisItem(
                            analysis.id,
                            analysis.predictedStatus,
                            analysis.updatedAt,
                            analysis.createdAt
                        )
                    }
                    if (localData != analysisList) {
                        analysisDao.clearAllAnalysis()
                        analysisDao.insertAllAnalysis(analysisList)
                        emit(Result.Success(Pair(analysisList, modePredictedStatus)))
                    }
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

    private fun calculateMode(values: List<String?>): String? {
        if (values.size < 7) {
            return null
        }

        return values
            .filterNotNull()
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
    }

}