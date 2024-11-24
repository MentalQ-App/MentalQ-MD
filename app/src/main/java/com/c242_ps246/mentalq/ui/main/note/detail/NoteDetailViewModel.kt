package com.c242_ps246.mentalq.ui.main.note.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import com.c242_ps246.mentalq.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.c242_ps246.mentalq.data.repository.Result

data class NoteDetailUiState(
    val isLoading: Boolean = false,
    val note: ListNoteItem? = null,
    val error: String? = null,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false
)

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _date = MutableStateFlow("")
    val date = _date.asStateFlow()

    private val _emotion = MutableStateFlow<String?>(null)
    val emotion = _emotion.asStateFlow()

    fun loadNote(noteId: String) {
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
                    _emotion.value = note.emotion
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

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
        scheduleUpdateNote()
    }

    fun updateContent(newContent: String) {
        _content.value = newContent
        scheduleUpdateNote()
    }

    fun updateEmotion(newEmotion: String) {
        _emotion.value = newEmotion
        scheduleUpdateNote()
    }

    private var updateJob: kotlinx.coroutines.Job? = null

    private fun scheduleUpdateNote() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            saveNote()
        }
    }

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

    private suspend fun saveNote() {
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