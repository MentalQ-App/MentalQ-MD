package com.c242_ps246.mentalq.data.local.remotemediator

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys (
    @PrimaryKey
    val noteId: String,
    val prevKey: Int?,
    val nextKey: Int?
)