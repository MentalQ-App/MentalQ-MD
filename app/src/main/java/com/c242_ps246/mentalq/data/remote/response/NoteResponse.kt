package com.c242_ps246.mentalq.data.remote.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class NoteResponse(
    @field:SerializedName("listNote")
    val listNote: List<ListNoteItem>? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class DeleteNoteResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class DetailNoteResponse(
    @field:SerializedName("note")
    val note: ListNoteItem? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

@Entity(tableName = "note")
@Parcelize
data class ListNoteItem(
    @PrimaryKey
    @field:SerializedName("note_id")
    val id: String,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("content")
    val content: String? = null,

    @field:SerializedName("content_normalized")
    val contentNormalized: String? = null,

    @field:SerializedName("predicted_status")
    val predictedStatus: String? = null,

    @field:SerializedName("confidence_score")
    val confidenceScore: Float? = null,

    @field:SerializedName("emotion")
    val emotion: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null
) : Parcelable
