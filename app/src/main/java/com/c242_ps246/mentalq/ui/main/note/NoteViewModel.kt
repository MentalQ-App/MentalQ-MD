package com.c242_ps246.mentalq.ui.main.note

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import com.c242_ps246.mentalq.data.repository.NoteRepository
import com.c242_ps246.mentalq.data.repository.Result
import com.c242_ps246.mentalq.ui.utils.Utils.fetchServerTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeParseException
import javax.inject.Inject

data class NoteScreenUiState(
    val isLoading: Boolean = true,
    val note: ListNoteItem? = null,
    val success: Boolean = false,
    val error: String? = null,
    val isCreatingNewNote: Boolean = false,
    val canAddNewNote: Boolean? = true
)

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _listNote = MutableStateFlow<List<ListNoteItem>?>(emptyList())
    val listNote = _listNote.asStateFlow()

    private val _navigateToNoteDetail = MutableStateFlow<String?>(null)
    val navigateToNoteDetail: StateFlow<String?> = _navigateToNoteDetail.asStateFlow()

    private val _todayDate = MutableStateFlow<LocalDate?>(null)

    init {
        loadAllNotes()
        updateTodayFromServer()
    }

    private fun updateTodayFromServer() {
        fetchServerTime(
            onTimeFetched = { serverTime ->
                val today = serverTime.toLocalDate()
                _todayDate.value = today
                Log.d("ServerDate", "Successfully updated today's date: $today")
            },
            onError = { error ->
                Log.e("ServerDate", "Failed to fetch server time: $error")
            }
        )
    }

    private fun isNoteTodayAlreadyAdded(): Boolean {
        val currentServerDate = _todayDate.value ?: return false
        val currentNoteList = _listNote.value ?: return false

        return currentNoteList.any { note ->
            note.createdAt?.let { createdAt ->
                try {
                    val instant = Instant.parse(createdAt)
                    val createdAtDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDate()

                    Log.d(
                        "NoteScreen",
                        "Detailed Check - Server Today: $currentServerDate, Note Date: $createdAtDateTime, Match: ${createdAtDateTime == currentServerDate}"
                    )

                    createdAtDateTime == currentServerDate
                } catch (e: DateTimeParseException) {
                    Log.e("NoteScreen", "Date parsing error: ${e.message}")
                    false
                }
            } == true
        }
    }

    fun loadAllNotes() {
        viewModelScope.launch {
            noteRepository.getAllNotes().observeForever { result ->
                when (result) {
                    Result.Loading -> {
                        Log.d("NoteViewModel", "loadAllNotes: Loading")
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        Log.d("NoteViewModel", "loadAllNotes: ${result.data}")
                        val notes = result.data
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null,
                            success = true
                        )
                        _listNote.value = notes
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.error
                        )
                    }
                }
            }
        }
    }

    fun addNote(note: ListNoteItem) {
        viewModelScope.launch {
            val cannotAddNote = isNoteTodayAlreadyAdded()
            if (cannotAddNote) {
                _uiState.value = _uiState.value.copy(
                    isCreatingNewNote = false,
                    isLoading = false,
                    canAddNewNote = false
                )
                return@launch
            }
            noteRepository.insertNote(note).observeForever { result ->
                when (result) {
                    Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isCreatingNewNote = true)
                    }

                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null,
                            success = true
                        )
                        _listNote.value = _listNote.value?.plus(result.data)
                        _navigateToNoteDetail.value = result.data.id
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.error
                        )
                    }
                }
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            noteRepository.deleteNoteById(noteId).observeForever { result ->
                when (result) {
                    Result.Loading -> {

                    }

                    is Result.Success -> {
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                error = null,
                                success = true,
                                canAddNewNote = true
                            )
                        _listNote.value = _listNote.value?.filter { it.id != noteId }
                    }

                    is Result.Error -> {
                        _uiState.value =
                            _uiState.value.copy(isLoading = false, error = result.error)
                    }
                }
            }
        }
    }

    fun navigateToNoteDetailCompleted() {
        _navigateToNoteDetail.value = null
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
