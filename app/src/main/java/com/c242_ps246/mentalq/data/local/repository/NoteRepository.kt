package com.c242_ps246.mentalq.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c242_ps246.mentalq.data.local.room.NoteDao
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import com.c242_ps246.mentalq.data.remote.retrofit.NoteApiService

class NoteRepository(
    private val noteDao: NoteDao,
    private val noteApiService: NoteApiService
) {
    fun getAllNotes(): LiveData<Result<List<ListNoteItem>>> = liveData {
        emit(Result.Loading)
        try {
            val localData = noteDao.getAllNotes()
            val response = noteApiService.getNotes()
            val notes = response.listNote

            if (localData.isNotEmpty() && localData == notes) {
                emit(Result.Success(localData))
            } else {
                try {
                    val noteList = notes!!.map { note ->
                        ListNoteItem(
                            note.id,
                            note.title,
                            note.content,
                            note.emotion,
                            note.updatedAt,
                            note.createdAt
                        )
                    }
                    noteDao.clearAllNotes()
                    noteDao.insertAllNotes(noteList)
                    emit(Result.Success(noteList))
                } catch (e: Exception) {
                    emit(Result.Error("An error occurred: ${e.message}"))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }

    }

    suspend fun getNoteById(noteId: String): ListNoteItem {
        return noteDao.getNoteById(noteId)
    }

    fun insertNote(note: ListNoteItem): LiveData<Result<ListNoteItem>> = liveData {
        emit(Result.Loading)
        try {
            val response = noteApiService.createNote(
                title = note.title ?: "",
                content = note.content ?: "",
                emotion = note.emotion ?: ""
            )
            if (response.error == false) {
                val newNote = ListNoteItem(
                    response.note?.id ?: "",
                    response.note?.title,
                    response.note?.content,
                    response.note?.emotion,
                    response.note?.updatedAt,
                    response.note?.createdAt
                )
                noteDao.insertNote(newNote)
                emit(Result.Success(newNote))
            } else {
                emit(Result.Error("An error occurred: ${response.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    suspend fun updateRemoteNote(note: ListNoteItem) {
        noteApiService.updateNote(
            id = note.id,
            title = note.title ?: "",
            content = note.content ?: "",
            emotion = note.emotion ?: ""
        )
    }

    suspend fun updateLocalNote(note: ListNoteItem) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNoteById(noteId: String) {
        noteDao.deleteNoteById(noteId)
    }
}
