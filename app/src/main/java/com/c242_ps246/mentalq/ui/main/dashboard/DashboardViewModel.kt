package com.c242_ps246.mentalq.ui.main.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps246.mentalq.data.repository.AuthRepository
import com.c242_ps246.mentalq.data.repository.NoteRepository
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import com.c242_ps246.mentalq.data.repository.Result
import com.c242_ps246.mentalq.data.manager.MentalQAppPreferences
import com.c242_ps246.mentalq.data.remote.response.UserData
import com.c242_ps246.mentalq.data.repository.AnalysisRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val authRepository: AuthRepository,
    private val analysisRepository: AnalysisRepository,
    private val preferencesManager: MentalQAppPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _listNote = MutableStateFlow<List<ListNoteItem>>(emptyList())
    val listNote = _listNote.asStateFlow()

    private val _streakInfo = MutableStateFlow(StreakInfo())
    val streakInfo = _streakInfo.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData = _userData.asStateFlow()

    private val _predictedStatusMode = MutableStateFlow<String?>(null)
    val predictedStatusMode = _predictedStatusMode.asStateFlow()

    fun loadLatestNotes() {
        noteRepository.getAllNotes().observeForever { result ->
            when (result) {
                Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }

                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    _listNote.value = result.data.take(5)
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
                }
            }
        }
    }

    fun getUserData() {
        authRepository.getUser().observeForever { result ->
            when (result) {
                Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }

                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    _userData.value = result.data
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun calculateStreak() {
        viewModelScope.launch {
            noteRepository.getAllNotes().observeForever { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        val sortedNotes = result.data.sortedByDescending { it.createdAt }
                        if (sortedNotes.isEmpty()) {
                            _streakInfo.value = StreakInfo()
                            return@observeForever
                        }
                        val dates = sortedNotes.mapNotNull { note ->
                            try {
                                val instant = Instant.parse(note.createdAt)
                                LocalDate.ofInstant(instant, ZoneId.systemDefault())
                            } catch (e: Exception) {
                                null
                            }
                        }

                        val streak = calculateDiaryStreak(dates)
                        _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                        _streakInfo.value = streak
                    }

                    else -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                        _streakInfo.value = StreakInfo()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDiaryStreak(dates: List<LocalDate>): StreakInfo {
        if (dates.isEmpty()) return StreakInfo()

        val today = LocalDate.now()
        val lastEntryDate = dates.first()

        if (ChronoUnit.DAYS.between(lastEntryDate, today) > 1) {
            return StreakInfo(
                currentStreak = 0,
                lastEntryDate = lastEntryDate
            )
        }

        var currentStreak = 1
        var previousDate = lastEntryDate

        for (i in 1 until dates.size) {
            val currentDate = dates[i]
            val daysBetween = ChronoUnit.DAYS.between(currentDate, previousDate)

            if (daysBetween == 1L) {
                currentStreak++
                previousDate = currentDate
            } else {
                break
            }
        }
        viewModelScope.launch {
            preferencesManager.saveStreakInfo(lastEntryDate.toString(), currentStreak)
        }

        return StreakInfo(
            currentStreak = currentStreak,
            lastEntryDate = lastEntryDate
        )
    }

    fun getPredictedStatusMode() {
        analysisRepository.getAnalysis().observeForever { result ->
            when (result) {
                Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }

                is Result.Success -> {
                    val (analysisList, mode) = result.data
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    _predictedStatusMode.value = mode
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

data class StreakInfo(
    val currentStreak: Int = 0,
    val lastEntryDate: LocalDate? = null
)