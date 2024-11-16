package com.c242_ps246.mentalq.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c242_ps246.mentalq.data.local.room.MentalQDatabase
import com.c242_ps246.mentalq.data.local.room.NoteDao
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import com.c242_ps246.mentalq.data.remote.retrofit.ApiService

class NoteRepository(
    private val mentalQDatabase: MentalQDatabase,
    private val noteDao: NoteDao,
    private val apiService: ApiService
) {

//    fun getAllNotes(): LiveData<PagingData<ListNoteItem>>{
//        @OptIn(ExperimentalPagingApi::class)
//        return Pager(
//            config = PagingConfig(
//                pageSize = 10,
//                enablePlaceholders = false
//            ),
//            remoteMediator = NoteRemoteMediator(mentalQDatabase, apiService),
//            pagingSourceFactory = { noteDao.getAllNotes() }
//        ).liveData
//    }

    fun getAllNotes(): LiveData<Result<List<ListNoteItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getNotes(userId = "1")
            val notes = response.listNote
            val noteList = notes!!.map { note ->
                ListNoteItem(
                    note.id,
                    note.title,
                    note.content,
                    note.date,
                    note.emotion
                )
            }
            noteDao.clearAllNotes()
            noteDao.insertAllNotes(noteList)
            emit(Result.Success(noteList))
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }
    suspend fun getNoteById(noteId: String): ListNoteItem {
        return noteDao.getNoteById(noteId)
    }

    suspend fun insertNote(note: ListNoteItem) {
        noteDao.insertNote(note)
    }

    suspend fun updateNote(note: ListNoteItem) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNoteById(noteId: String) {
        noteDao.deleteNoteById(noteId)
    }
}
