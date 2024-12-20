package com.c242_ps246.mentalq.ui.main.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.c242_ps246.mentalq.data.manager.MentalQAppPreferences
import com.c242_ps246.mentalq.data.remote.response.UserData
import com.c242_ps246.mentalq.data.repository.AuthRepository
import com.c242_ps246.mentalq.data.repository.Result
import com.c242_ps246.mentalq.data.repository.UserRepository
import com.c242_ps246.mentalq.ui.auth.AuthScreenUIState
import com.c242_ps246.mentalq.ui.notification.dailyreminder.DailyReminderNotificationHelper.Companion.DAILY_REMINDER_WORK_NAME
import com.c242_ps246.mentalq.ui.notification.dailyreminder.DailyReminderWorker
import com.c242_ps246.mentalq.ui.notification.streak.StreakNotificationHelper
import com.c242_ps246.mentalq.ui.notification.streak.StreakWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val preferencesManager: MentalQAppPreferences,
    private val workManager: WorkManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthScreenUIState())
    val uiState = _uiState.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData = _userData.asStateFlow()

    val notificationsEnabled = preferencesManager.getNotificationsState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun setNotificationsEnabled(enabled: Boolean, context: Context) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
            if (enabled) {
                StreakWorker.scheduleNextNotification(context)
                scheduleReminder()
            } else {
                workManager.cancelUniqueWork(StreakNotificationHelper.WORK_NAME)
                cancelReminder()
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

    fun updateProfile(
        name: RequestBody,
        email: RequestBody,
        birthday: RequestBody,
        profileImage: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            userRepository.updateProfile(name, email, birthday, profileImage)
                .observeForever { result ->
                    when (result) {
                        Result.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }

                        is Result.Success -> {
                            _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                            _userData.value = result.data
                        }

                        is Result.Error -> {
                            _uiState.value =
                                _uiState.value.copy(isLoading = false, error = result.error)
                        }
                    }
                }
        }
    }

    private fun scheduleReminder() {
        val reminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            DAILY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )
    }

    private fun cancelReminder() {
        workManager.cancelUniqueWork(DAILY_REMINDER_WORK_NAME)
    }
}
