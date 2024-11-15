package com.c242_ps246.mentalq.data.remote.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class NoteResponse (
    @field:SerializedName("listNote")
    val listNote: List<ListNoteItem>? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

@Entity(tableName = "note")
data class ListNoteItem (
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("content")
    val content: String,

    @field:SerializedName("date")
    val date: String,

    @field:SerializedName("emotion")
    val emotion: String?
)

