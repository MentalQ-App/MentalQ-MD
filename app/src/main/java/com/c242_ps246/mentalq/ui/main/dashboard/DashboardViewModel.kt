package com.c242_ps246.mentalq.ui.main.dashboard

import androidx.lifecycle.ViewModel
import com.c242_ps246.mentalq.data.local.repository.NoteRepository
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.c242_ps246.mentalq.data.local.repository.Result

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _listNote = MutableStateFlow<List<ListNoteItem>>(emptyList())
    val listNote = _listNote.asStateFlow()

    fun loadLatestNotes() {
        noteRepository.getAllNotes().observeForever { result ->
            when (result) {
                Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }

                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    _listNote.value = result.data.take(3)
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
                }
            }
        }
    }
}

data class DashboardScreenUiState(
    val isLoading: Boolean = true,
    val note: ListNoteItem? = null,
    val error: String? = null
)