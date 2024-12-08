package com.c242_ps246.mentalq.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c242_ps246.mentalq.data.local.room.NoteDao
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import com.c242_ps246.mentalq.data.remote.retrofit.NoteApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRepository(
    private val noteDao: NoteDao,
    private val noteApiService: NoteApiService
) {
    fun getAllNotes(): LiveData<Result<List<ListNoteItem>>> = liveData {
        emit(Result.Loading)
        try {
            val localData = noteDao.getAllNotes()
            emit(Result.Success(localData.sortedByDescending { it.createdAt }))

            try {
                val response = noteApiService.getNotes()
                val remoteNotes = response.listNote

                if (remoteNotes != null) {
                    val noteList = remoteNotes.map { note ->
                        ListNoteItem(
                            note.id,
                            note.title,
                            note.content,
                            note.emotion,
                            note.updatedAt,
                            note.createdAt
                        )
                    }
                    if (localData != noteList) {
                        noteDao.clearAllNotes()
                        noteDao.insertAllNotes(noteList)
                        emit(Result.Success(noteList.sortedByDescending { it.createdAt }))
                    }
                }
            } catch (e: Exception) {
                if (localData.isEmpty()) {
                    Log.d("NoteRepository", "Failed to fetch remote data: ${e.message}")
                    emit(Result.Error("Failed to fetch remote data, check your internet connection and try again."))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error("Database error: ${e.message}"))
        }
    }

    suspend fun getNoteById(noteId: String): ListNoteItem? = withContext(Dispatchers.IO) {
        try {
            noteDao.getNoteById(noteId)
        } catch (e: Exception) {
            Log.d("NoteRepository", "Database error: ${e.message}")
            null
        }
    }

    fun insertNote(note: ListNoteItem): LiveData<Result<ListNoteItem>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = noteApiService.createNote(
                title = note.title ?: "",
                content = note.content ?: "",
                emotion = note.emotion ?: ""
            )
            if (response.error == false && response.note != null) {
                val newNote = ListNoteItem(
                    id = response.note.id,
                    title = response.note.title,
                    content = response.note.content,
                    emotion = response.note.emotion,
                    updatedAt = response.note.updatedAt,
                    createdAt = response.note.createdAt
                )
                noteDao.insertNote(newNote)
                emit(Result.Success(newNote))
            } else {
                emit(Result.Error("Failed to create note: ${response.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    suspend fun updateNote(note: ListNoteItem): Result<ListNoteItem> = withContext(Dispatchers.IO) {
        try {
            val response = noteApiService.updateNote(
                id = note.id,
                title = note.title ?: "",
                content = note.content ?: "",
                emotion = note.emotion ?: ""
            )

            if (response.error == false) {
                noteDao.updateNote(note)
                Result.Success(note)
            } else {
                Result.Error("Failed to update note: ${response.message}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    fun deleteNoteById(noteId: String): LiveData<Result<String>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = noteApiService.deleteNote(id = noteId)
            if (response.error == false) {
                noteDao.deleteNoteById(noteId)
                emit(Result.Success(response.message ?: "Note deleted successfully"))
            } else {
                emit(Result.Error("Failed to delete note: ${response.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    suspend fun getLastNote(): ListNoteItem? {
        return withContext(Dispatchers.IO) {
            try {
                noteDao.getLastNote()
            } catch (e: Exception) {
                Log.d("NoteRepository", "Database error: ${e.message}")
                null
            }
        }
    }
}