package com.c242_ps246.mentalq.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.c242_ps246.mentalq.data.remote.response.ListAnalysisItem

@Dao
interface AnalysisDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnalysis(analysis: List<ListAnalysisItem>)

    @Query("SELECT * FROM analysis")
    suspend fun getAllAnalysis(): List<ListAnalysisItem>

    @Query("DELETE FROM analysis")
    suspend fun clearAllAnalysis()
}