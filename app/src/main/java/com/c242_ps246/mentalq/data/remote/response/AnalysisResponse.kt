package com.c242_ps246.mentalq.data.remote.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class AnalysisResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("listAnalysis")
    val listAnalysis: List<ListAnalysisItem>
)

@Entity(tableName = "analysis")
data class ListAnalysisItem(
    @PrimaryKey
    @field:SerializedName("analysis_id")
    val id: String,

    @field:SerializedName("predicted_status")
    val predictedStatus: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null
)