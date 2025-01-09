package com.c242_ps246.mentalq.ui.main.note.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import com.c242_ps246.mentalq.data.repository.NoteRepository
import com.c242_ps246.mentalq.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteDetailUiState(
    val isLoading: Boolean = false,
    val note: ListNoteItem? = null,
    val error: String? = null,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false
)

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _title = MutableStateFlow(savedStateHandle.get<String>("title") ?: "")
    val title: StateFlow<String> = _title

    private val _content = MutableStateFlow(savedStateHandle.get<String>("content") ?: "")
    val content: StateFlow<String> = _content

    private val _emotion = MutableStateFlow(savedStateHandle.get<String>("emotion") ?: "")
    val emotion: StateFlow<String> = _emotion

    private val _date = MutableStateFlow("")
    val date = _date.asStateFlow()
    
    private var currentNoteId: String? = null

    init {
        viewModelScope.launch {
            _title.collect { savedStateHandle["title"] = it }
        }
        viewModelScope.launch {
            _content.collect { savedStateHandle["content"] = it }
        }
        viewModelScope.launch {
            _emotion.collect { savedStateHandle["emotion"] = it }
        }
        viewModelScope.launch {
            _date.collect { savedStateHandle["date"] = it }
        }
    }

    fun loadNote(noteId: String) {
        if (currentNoteId != noteId || _title.value.isEmpty()) {
            currentNoteId = noteId
            savedStateHandle["noteId"] = noteId

            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                try {
                    val note = noteRepository.getNoteById(noteId)
                    if (note != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            note = note,
                            error = null
                        )

                        _title.value = note.title ?: ""
                        _content.value = note.content ?: ""
                        _date.value = note.createdAt ?: ""
                        _emotion.value = note.emotion ?: ""
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Note not found"
                        )
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load note: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
        saveNote()
    }

    fun updateContent(newContent: String) {
        _content.value = newContent
        saveNote()
    }

    fun updateEmotion(newEmotion: String) {
        _emotion.value = newEmotion
        saveNote()
    }

    private var updateJob: kotlinx.coroutines.Job? = null

    fun saveNoteImmediately() {
        viewModelScope.launch {
            updateJob?.cancel()

            _uiState.value.note?.let { currentNote ->
                val updatedNote = currentNote.copy(
                    title = _title.value,
                    content = _content.value,
                    emotion = _emotion.value
                )

                try {
                    _uiState.value = _uiState.value.copy(isSaving = true, error = null)

                    when (val result = noteRepository.updateNote(updatedNote)) {
                        is Result.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isSaving = false,
                                isSuccess = true,
                                note = result.data,
                                error = null
                            )
                        }

                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isSaving = false,
                                isSuccess = false,
                                error = result.error
                            )
                        }

                        Result.Loading -> {
                            _uiState.value = _uiState.value.copy(isSaving = true)
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isSuccess = false,
                        error = "Failed to save note: ${e.message}"
                    )
                }
            }
        }
    }

    private fun saveNote() {
        _uiState.value.note?.let { currentNote ->
            val updatedNote = currentNote.copy(
                title = _title.value,
                content = _content.value,
                emotion = _emotion.value
            )
            _uiState.value = _uiState.value.copy(note = updatedNote)
        }
    }
}