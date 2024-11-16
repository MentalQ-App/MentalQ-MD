package com.c242_ps246.mentalq.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotes(notes: List<ListNoteItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ListNoteItem)

    @Update
    suspend fun updateNote(note: ListNoteItem)

//    @Query("SELECT * FROM note")
//    fun getAllNotes(): PagingSource<Int, ListNoteItem>

    @Query("SELECT * FROM note")
    suspend fun getAllNotes(): List<ListNoteItem>

    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun getNoteById(id: String): ListNoteItem

    @Query("DELETE FROM note")
    suspend fun clearAllNotes()

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun deleteNoteById(id: String)
}